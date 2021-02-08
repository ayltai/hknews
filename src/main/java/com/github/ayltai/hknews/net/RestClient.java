package com.github.ayltai.hknews.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.http.conn.SdkConnectionKeepAliveStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.model.rss.Root;
import com.google.gson.Gson;

import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultBackoffStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class RestClient {
    private static final String  WEN_WEI_PO_ENDPOINT = "news.wenweipo.com";
    private static final Charset CHARSET_BIG5        = Charset.forName("Big5");

    private static final Gson GSON = new Gson();

    private static final CloseableHttpClient CLIENT = HttpClients.custom()
        .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
        .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
        .setDefaultCookieStore(new BasicCookieStore())
        .setDefaultRequestConfig(RequestConfig.custom()
            .setContentCompressionEnabled(true)
            .setConnectionRequestTimeout(Configuration.DEFAULT.getConnectTimeout())
            .setRedirectsEnabled(true)
            .setSocketTimeout(Configuration.DEFAULT.getSocketTimeout() * 1000)
            .build())
        .setDefaultSocketConfig(SocketConfig.custom()
            .setSoTimeout(Configuration.DEFAULT.getSocketTimeout() * 1000)
            .build())
        .setKeepAliveStrategy(new SdkConnectionKeepAliveStrategy(Configuration.DEFAULT.getIdleTimeout()))
        .setMaxConnTotal(Configuration.DEFAULT.getConnectionPoolSize())
        .setRedirectStrategy(new DefaultRedirectStrategy())
        .setRetryHandler(new DefaultHttpRequestRetryHandler())
        .setUserAgent(Configuration.DEFAULT.getUserAgent())
        .build();

    public String get(@NotNull final String url) throws IOException {
        return RestClient.request(new HttpGet(RestClient.escapeUrl(url)), null);
    }

    public <T> T get(@NotNull final String url, @NotNull final Class<T> clazz) throws IOException {
        return RestClient.request(new HttpGet(RestClient.escapeUrl(url)), clazz);
    }

    public <T> T post(@NotNull final String url, @NotNull final Map<String, String> fields) throws IOException {
        final HttpPost request = new HttpPost(RestClient.escapeUrl(url));
        request.setEntity(new UrlEncodedFormEntity(fields.entrySet().stream().map(field -> new BasicNameValuePair(field.getKey(), field.getValue())).collect(Collectors.toList())));

        return RestClient.request(request, null);
    }

    private <T> T request(@NotNull final HttpUriRequest request, @Nullable final Class<T> clazz) throws IOException {
        try (CloseableHttpResponse response = RestClient.CLIENT.execute(request)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) return clazz == null ? (T)EntityUtils.toString(response.getEntity(), RestClient.WEN_WEI_PO_ENDPOINT.equals(request.getURI().getHost()) ? RestClient.CHARSET_BIG5 : StandardCharsets.UTF_8) : clazz.isAssignableFrom(Root.class) ? new XmlMapper().readValue(EntityUtils.toString(response.getEntity(), RestClient.WEN_WEI_PO_ENDPOINT.equals(request.getURI().getHost()) ? RestClient.CHARSET_BIG5 : StandardCharsets.UTF_8), clazz) : RestClient.GSON.fromJson(EntityUtils.toString(response.getEntity(), RestClient.WEN_WEI_PO_ENDPOINT.equals(request.getURI().getHost()) ? RestClient.CHARSET_BIG5 : StandardCharsets.UTF_8), clazz);

            throw new ClientProtocolException("Received HTTP " + response.getStatusLine().getStatusCode());
        }
    }

    @NotNull
    private static String escapeUrl(@NotNull final String url) throws IOException {
        final URL uri = new URL(url);

        try {
            return new URI(uri.getProtocol(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getRef()).toASCIIString();
        } catch (final URISyntaxException e) {
            throw new IOException(e);
        }
    }
}

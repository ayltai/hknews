package com.github.ayltai.hknews.net;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.MainConfiguration;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Component
public final class DefaultContentServiceFactory implements ContentServiceFactory {
    private final MainConfiguration configuration;

    @Autowired
    public DefaultContentServiceFactory(@NonNull final MainConfiguration configuration) {
        this.configuration = configuration;
    }

    @NonNull
    public ContentService create() {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        return new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://github.com")
            .client(new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(this.configuration.getConnectionPoolSize(), this.configuration.getIdleTimeout(), TimeUnit.SECONDS))
                .connectTimeout(this.configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(this.configuration.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(this.configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request()
                    .newBuilder()
                    .header("User-Agent", this.configuration.getUserAgent())
                    .build()))
                .addInterceptor(chain -> {
                    final Response response = chain.proceed(chain.request());

                    if (chain.request().url().host().contains("news.wenweipo.com")) {
                        final ResponseBody body = response.body();

                        return body == null ? response : response.newBuilder()
                            .body(ResponseBody.create(new String(body.bytes(), "Big5"), body.contentType()))
                            .build();
                    }

                    return response;
                })
                .build())
            .build()
            .create(ContentService.class);
    }
}

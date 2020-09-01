package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.RssFeed;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.simpleframework.xml.core.Persister;

import retrofit2.Call;
import retrofit2.Response;

public final class ScmpParserTests extends ParserTests {
    @Test
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/scmp.xml"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call              call     = Mockito.mock(Call.class);
            final Response<RssFeed> response = Response.success(new Persister().read(RssFeed.class, bufferedReader.lines().collect(Collectors.joining("\n"))));

            Mockito.doReturn(call).when(service).getFeed("https://www.scmp.com/rss/2/feed");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new ScmpParser("南華早報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(50, items.size(), "Incorrect item count");
            Assertions.assertEquals("US sanctions Hong Kong leader Carrie Lam for ‘implementing Beijing’s policies of suppression’", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/scmp.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www.scmp.com/news/hong-kong/politics/article/3096455/hong-kong-national-security-law-us-consulate-city-breaks");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://www.scmp.com/news/hong-kong/politics/article/3096455/hong-kong-national-security-law-us-consulate-city-breaks");

            final Item updatedItem = new ScmpParser("南華早報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("In rare move, diplomatic office also hits back at allegations staff had colluded with opposition camp", updatedItem.getDescription().substring(0, 101), "Incorrect item description");
            Assertions.assertEquals(3, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("The US consulate on Garden Road in Hong Kong’s Central. Photo: Dickson Lee", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://cdn.i-scmp.com/sites/default/files/d8/images/methode/2020/08/07/bb5a9888-d87c-11ea-a9df-dfa023813e67_image_hires_183343.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
            Assertions.assertEquals(1, updatedItem.getVideos().size(), "Incorrect video count");
            Assertions.assertEquals("https://www.youtube.com/embed/hv4i9KfuRF0", updatedItem.getVideos().get(0).getUrl(), "Incorrect video URL");
            Assertions.assertEquals("https://www.youtube.com/embed/hv4i9KfuRF0/hqdefault.jpg", updatedItem.getVideos().get(0).getCover(), "Incorrect thumbnail URL");
        }
    }
}

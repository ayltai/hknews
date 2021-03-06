package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.rss.Root;
import com.github.ayltai.hknews.net.ContentService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class ScmpParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws Exception {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/scmp.xml"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(new XmlMapper().readValue(bufferedReader.lines().collect(Collectors.joining("\n")), Root.class)).when(service).getRss("https://www.scmp.com/rss/2/feed");

            final Collection<Item> items = new ScmpParser("南華早報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(50, items.size(), "Incorrect item count");
            Assertions.assertEquals("US sanctions Hong Kong leader Carrie Lam for ‘implementing Beijing’s policies of suppression’", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/scmp.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://www.scmp.com/news/hong-kong/politics/article/3096455/hong-kong-national-security-law-us-consulate-city-breaks");

            final Item item = new Item();
            item.setUrl("https://www.scmp.com/news/hong-kong/politics/article/3096455/hong-kong-national-security-law-us-consulate-city-breaks");

            final Item updatedItem = new ScmpParser("南華早報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("Hong Kong responds to the US consulate’s statement on the national security law<br>In rare move", updatedItem.getDescription().substring(0, 95), "Incorrect item description");
            Assertions.assertEquals(3, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("The US consulate on Garden Road in Hong Kong’s Central. Photo: Dickson Lee", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://cdn.i-scmp.com/sites/default/files/d8/images/methode/2020/08/08/bb5a9888-d87c-11ea-a9df-dfa023813e67_image_hires_021151.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
            Assertions.assertEquals(1, updatedItem.getVideos().size(), "Incorrect video count");
            Assertions.assertEquals("https://www.youtube.com/embed/hv4i9KfuRF0", updatedItem.getVideos().get(0).getUrl(), "Incorrect video URL");
            Assertions.assertEquals("https://www.youtube.com/embed/hv4i9KfuRF0/hqdefault.jpg", updatedItem.getVideos().get(0).getCover(), "Incorrect thumbnail URL");
        }
    }
}

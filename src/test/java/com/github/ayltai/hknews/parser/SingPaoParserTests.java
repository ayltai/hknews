package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import retrofit2.Call;
import retrofit2.Response;

public final class SingPaoParserTests extends ParserTests {
    @Test
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new SingPaoParser("成報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(20, items.size(), "Incorrect item count");
            Assertions.assertEquals("美國擴「潔淨網絡」 中國不可信程式須下架 限制華企雲端服務 騰訊 阿里被點名", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1&id=113658");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://www.singpao.com.hk/index.php?fi=news1&id=113658");

            final Item updatedItem = new SingPaoParser("成報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("【本報記者報道】美國國務卿蓬佩奧宣布擴大「潔淨網絡」（Clean Network）行動，以保護美國的", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(3, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("美國國務卿蓬佩奧宣布擴大「乾淨網絡」計劃，以保護", updatedItem.getImages().get(1).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://www.singpao.com.hk/image_upload/1596752020.jpg", updatedItem.getImages().get(1).getUrl(), "Incorrect image URL");
        }
    }
}

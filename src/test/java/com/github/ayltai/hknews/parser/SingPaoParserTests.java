package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class SingPaoParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1");

            final Collection<Item> items = new SingPaoParser("成報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(20, items.size(), "Incorrect item count");
            Assertions.assertEquals("美國擴「潔淨網絡」 中國不可信程式須下架 限制華企雲端服務 騰訊 阿里被點名", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1&id=113658");

            final Item item = new Item();
            item.setUrl("https://www.singpao.com.hk/index.php?fi=news1&id=113658");

            final Item updatedItem = new SingPaoParser("成報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("【本報記者報道】美國國務卿蓬佩奧宣布擴大「潔淨網絡」（Clean Network）行動，以保護美國的", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(3, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("美國國務卿蓬佩奧宣布擴大「乾淨網絡」計劃，以保護", updatedItem.getImages().get(1).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://www.singpao.com.hk/image_upload/1596752020.jpg", updatedItem.getImages().get(1).getUrl(), "Incorrect image URL");
        }
    }
}

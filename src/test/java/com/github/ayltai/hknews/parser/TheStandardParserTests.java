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

public final class TheStandardParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/thestandard_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).postHtml("https://www.thestandard.com.hk/ajax_sections_list.php", 4, 1);

            final Collection<Item> items = new TheStandardParser("英文虎報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(20, items.size(), "Incorrect image count");
            Assertions.assertEquals("Voluntary virus tests for all in two weeks", items.toArray(new Item[0])[0].getTitle(), "Incorrect item description");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/thestandard_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://www.thestandard.com.hk/breaking-news/section/4/152604/Voluntary-virus-tests-for-all-in-two-weeks");

            final Item item = new Item();
            item.setUrl("https://www.thestandard.com.hk/breaking-news/section/4/152604/Voluntary-virus-tests-for-all-in-two-weeks");

            final Item updatedItem = new TheStandardParser("英文虎報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("The Chief Executive Carrie Lam Cheng Yuet-ngor&nbsp;announced on today that the government", updatedItem.getDescription().substring(0, 90), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertNull(updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://www.thestandard.com.hk/images/instant_news/20200807/20200807155011152604contentPhoto1.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

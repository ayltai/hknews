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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public final class WenWeiPoParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/wenweipo_list.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml(ArgumentMatchers.anyString());

            final Collection<Item> items = new WenWeiPoParser("文匯報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(32 * 2, items.size(), "Incorrect image count");
            Assertions.assertEquals("47疑犯今提堂　警舉藍旗警告聚集者", items.iterator().next().getTitle(), "Incorrect item description");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/wenweipo_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("http://news.wenweipo.com/2020/08/09/IN2008090047.htm");

            final Item item = new Item();
            item.setUrl("http://news.wenweipo.com/2020/08/09/IN2008090047.htm");

            final Item updatedItem = new WenWeiPoParser("文匯報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("大公文匯全媒體報道：警方國家安全處於今年初就發起或參與反對派所謂的「35+」初選拘捕55人，並對當中", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(2, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("逾百人受陳皓桓煽惑，聚集法院外。（大公文匯全媒體記者麥鈞傑攝）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://dw-media.wenweipo.com/dams/share/image/202103/01/603c5154e4b003831df87ce91.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

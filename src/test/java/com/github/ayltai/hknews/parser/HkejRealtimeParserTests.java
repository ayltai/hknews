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

public final class HkejRealtimeParserTests extends ParserTests {
    @Test
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hkej_realtime_list.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www2.hkej.com/instantnews/current");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new HkejRealtimeParser("信報即時", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(27, items.size(), "Incorrect item count");
            Assertions.assertEquals("特首委任林大輝為香港電台顧問委員會主席", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hkej_realtime_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www2.hkej.com/instantnews/current/article/2552130/%E7%89%B9%E9%A6%96%E5%A7%94%E4%BB%BB%E6%9E%97%E5%A4%A7%E8%BC%9D%E7%82%BA%E9%A6%99%E6%B8%AF%E9%9B%BB%E5%8F%B0%E9%A1%A7%E5%95%8F%E5%A7%94%E5%93%A1%E6%9C%83%E4%B8%BB%E5%B8%AD");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://www2.hkej.com/instantnews/current/article/2552130/%E7%89%B9%E9%A6%96%E5%A7%94%E4%BB%BB%E6%9E%97%E5%A4%A7%E8%BC%9D%E7%82%BA%E9%A6%99%E6%B8%AF%E9%9B%BB%E5%8F%B0%E9%A1%A7%E5%95%8F%E5%A7%94%E5%93%A1%E6%9C%83%E4%B8%BB%E5%B8%AD");

            final Item updatedItem = new HkejRealtimeParser("信報即時", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("政府刊憲，行政長官委任林大輝為香港電台顧問委員會主席，下月1日起生效，任期兩年。<br><br>另外，新委任趙應春及蘇紹聰為委員會成員，並再度委任其餘10名成員，任期同樣為兩年。", updatedItem.getDescription(), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("特首委任林大輝為香港電台顧問委員會主席。(信報資料圖片)", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://static.hkej.com/hkej/images/2020/08/14/2552130_d989dbe230eb0c1d8d866188e1654e88.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

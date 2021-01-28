package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

public final class OrientalDailyParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_list.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml(ArgumentMatchers.anyString());
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new OrientalDailyParser("東方日報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(63, items.size(), "Incorrect item count");
            Assertions.assertEquals("街市重開 鼠患不改", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://orientaldaily.on.cc/cnt/news/20200809/00174_001.html");
            Mockito.doReturn(response).when(call).execute();
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_videolist.xml"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://orientaldaily.on.cc/cnt/keyinfo/20200809/videolist.xml");
            Mockito.doReturn(response).when(call).execute();
        }

        final Item item = new Item();
        item.setUrl("https://orientaldaily.on.cc/cnt/news/20200809/00174_001.html");
        item.setPublishDate(Date.from(LocalDate.parse("20200809", DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        final Item updatedItem = new OrientalDailyParser("東方日報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).updateItem(item);

        Assertions.assertEquals("大鼠戊肝和新冠肺炎夾擊，家庭主婦到街市買餸高危，隨時連菜帶毒回家傳家人！出現多宗確診個案的紅磡及土瓜", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
        Assertions.assertEquals(5, updatedItem.getImages().size(), "Incorrect image count");
        Assertions.assertEquals("紅磡街市：昨日重開的紅磡街市已現鼠蹤。（李華輝攝）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
        Assertions.assertEquals("https://orientaldaily.on.cc/cnt/news/20200809/photo/0809-00174-001b1.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        Assertions.assertEquals(1, updatedItem.getVideos().size(), "Incorrect video count");
        Assertions.assertEquals("https://video-cdn.on.cc/Video/202008/ONS200809-14079-01-nicam-M_ipad.mp4", updatedItem.getVideos().get(0).getUrl(), "Incorrect video URL");
        Assertions.assertEquals("https://tv.on.cc/xml/Thumbnail/202008/bigthumbnail/ONS200809-14079-01-nicam-M.jpg", updatedItem.getVideos().get(0).getCover(), "Incorrect thumbnail URL");
    }
}

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

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import retrofit2.Call;
import retrofit2.Response;

public final class OrientalDailyRealtimeParserTests extends ParserTests {
    @Test
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_realtime_list.js"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml(ArgumentMatchers.anyString());
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new OrientalDailyRealtimeParser("東方即時", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(63, items.size(), "Incorrect item count");
            Assertions.assertEquals("九龍灣耆康會啟業安老院恐全院爆疫　約50院友陸續緊急撤離", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_realtime_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hk.on.cc/cnt/news/20200809/bkn-20200809231616005-0809_00822_001.html");
            Mockito.doReturn(response).when(call).execute();
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/orientaldaily_realtime_videolist.js"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hk.on.cc/hk/bkn/video/20200809/articleVideo_news.js");
            Mockito.doReturn(response).when(call).execute();
        }

        final Item item = new Item();
        item.setUrl("https://hk.on.cc/cnt/news/20200809/bkn-20200809231616005-0809_00822_001.html");
        item.setPublishDate(Date.from(LocalDate.parse("20200809", DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        final Item updatedItem = new OrientalDailyRealtimeParser("東方即時", this.sourceService, factory).updateItem(item);

        Assertions.assertEquals("衞生署今日(9日)指，早前有院友確診的九龍灣耆康會啟業護理安老院，再多2人確診及1人初步確診，或屬全院爆發，會安排撤離院友。晚上10時許，身穿全套防護裝備的醫療輔助隊人員到達該院舍，陸續將院友撤離，首批3車合共7人會送去亞洲博覽館社區治療設施暫住。現場消息指，該院舍仍有逾40人在等候送往亞博館，醫療輔助隊人員稱需等候衞生署指示。<br><br>衞生署衞生防護中心傳染病處主任張竹君今日在疫情記者會指，耆康會啟業護理安老院再有2人確診及1人初步確診，其中一名確診者與早前確診的老翁同房，而另一名確診及1名初步確診患者，均住不同房間，由於現時已涉及3間房，或屬全院爆發，全院40多人均列為密切接觸者，需撤離院友作檢疫，員工亦需檢疫隔離。<br><br>《香港疫慌》專頁: http://hk.on.cc/fea/hkdisease", updatedItem.getDescription(), "Incorrect item description");
        Assertions.assertEquals(2, updatedItem.getImages().size(), "Incorrect image count");
        Assertions.assertEquals("醫療輔助隊人員撤離院友。(張福宏攝)", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
        Assertions.assertEquals("https://hk.on.cc/hk/bkn/cnt/news/20200809/photo/bkn-20200809231616005-0809_00822_001_01b.jpg?20200809233314", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        Assertions.assertEquals(1, updatedItem.getVideos().size(), "Incorrect video count");
        Assertions.assertEquals("https://video-cdn.on.cc/Video/202008/ONS200810-14390-03-M_ipad.mp4", updatedItem.getVideos().get(0).getUrl(), "Incorrect video URL");
        Assertions.assertEquals("https://hk.on.cc/hk/bkn/cnt/news/20200809/photo/bkn-20200809231616005-0809_00822_001_01p.jpg", updatedItem.getVideos().get(0).getCover(), "Incorrect thumbnail URL");
    }
}

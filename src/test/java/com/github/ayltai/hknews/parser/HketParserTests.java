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

public final class HketParserTests extends ParserTests {
    @Test
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hket.xml"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call              call     = Mockito.mock(Call.class);
            final Response<RssFeed> response = Response.success(new Persister().read(RssFeed.class, bufferedReader.lines().collect(Collectors.joining("\n"))));

            Mockito.doReturn(call).when(service).getFeed("https://www.hket.com/rss/hongkong");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new HketParser("經濟日報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(24, items.size(), "Incorrect item count");
            Assertions.assertEquals("【新冠肺炎】機動部隊23歲男警初步確診　同組訓練40多名警員或要檢疫", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hket.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://topick.hket.com/article/2719226");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://topick.hket.com/article/2719226");

            final Item updatedItem = new HketParser("經濟日報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("本港第三波新冠肺炎疫情仍然嚴峻，衞生防護中心公布本港今日（9日）新增72宗確診個案，9宗是輸入個案，", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("張竹君表示，初步確診男警於粉嶺蝴蝶山機動部隊訓練機地進行培訓，涉及170人。（程志遠攝）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://static02-proxy.hket.com/res/v3/image/content/2715000/2719226/60bd95fb-4332-40a4-b93f-d9a940bb10c6_1024.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

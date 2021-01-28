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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.simpleframework.xml.core.Persister;

import retrofit2.Call;
import retrofit2.Response;

public final class MingPaoParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/mingpao.xml"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call              call     = Mockito.mock(Call.class);
            final Response<RssFeed> response = Response.success(new Persister().read(RssFeed.class, bufferedReader.lines().collect(Collectors.joining("\n"))));

            Mockito.doReturn(call).when(service).getFeed(ArgumentMatchers.anyString());
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new ScmpParser("明報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(82 + 82, items.size(), "Incorrect item count");
            Assertions.assertEquals("全民自願驗 林鄭認無禁足成效打折 涉款料可達24億 3內地機構承包", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/mingpao.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://news.mingpao.com/pns/%e6%b8%af%e8%81%9e/article/20200808/s00002/1596825748734/%e5%85%a8%e6%b0%91%e8%87%aa%e9%a1%98%e9%a9%97-%e6%9e%97%e9%84%ad%e8%aa%8d%e7%84%a1%e7%a6%81%e8%b6%b3%e6%88%90%e6%95%88%e6%89%93%e6%8a%98-%e6%b6%89%e6%ac%be%e6%96%99%e5%8f%af%e9%81%9424%e5%84%84-3%e5%85%a7%e5%9c%b0%e6%a9%9f%e6%a7%8b%e6%89%bf%e5%8c%85");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://news.mingpao.com/pns/%e6%b8%af%e8%81%9e/article/20200808/s00002/1596825748734/%e5%85%a8%e6%b0%91%e8%87%aa%e9%a1%98%e9%a9%97-%e6%9e%97%e9%84%ad%e8%aa%8d%e7%84%a1%e7%a6%81%e8%b6%b3%e6%88%90%e6%95%88%e6%89%93%e6%8a%98-%e6%b6%89%e6%ac%be%e6%96%99%e5%8f%af%e9%81%9424%e5%84%84-3%e5%85%a7%e5%9c%b0%e6%a9%9f%e6%a7%8b%e6%89%bf%e5%8c%85");

            final Item updatedItem = new MingPaoParser("明報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("<p>【明報專訊】政府昨公布兩星期後會展開「普及社區檢測」，市民可自願參加新型冠狀病毒檢測，並指定由3間在", updatedItem.getDescription().substring(0, 53), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("政府選定了位於西區的中山紀念公園體育館，供華大基因設立臨時「氣膜實驗室」。實驗室設計如同華大在內地設立的檢測設施，華大指其合資公司「華昇診斷中心」在體育館內搭建16個帳篷，預計將在兩周內啟用，並可每日做10萬個單管測試，將華昇診斷中心的總檢測量由每日3萬提升至13萬。（政府新聞處相片）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://fs.mingpao.com/pns/20200808/s00007/e687eee5f0c0cff4b8a0ee61327266d6.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

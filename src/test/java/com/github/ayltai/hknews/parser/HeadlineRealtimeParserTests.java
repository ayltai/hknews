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
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

public final class HeadlineRealtimeParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_realtime_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hd.stheadline.com/news/realtime/hk/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E/");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new HeadlineRealtimeParser("頭條即時", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(10, items.size(), "Incorrect item count");
            Assertions.assertEquals("【美國制裁】特區政府嚴批厚顏無恥及卑鄙 譴責公開官員個人資料", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_realtime_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hd.stheadline.com/news/realtime/hk/1840833/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E-%E7%BE%8E%E5%9C%8B%E5%88%B6%E8%A3%81-%E7%89%B9%E5%8D%80%E6%94%BF%E5%BA%9C%E5%9A%B4%E6%89%B9%E5%8E%9A%E9%A1%8F%E7%84%A1%E6%81%A5%E5%8F%8A%E5%8D%91%E9%84%99-%E8%AD%B4%E8%B2%AC%E5%85%AC%E9%96%8B%E5%AE%98%E5%93%A1%E5%80%8B%E4%BA%BA%E8%B3%87%E6%96%99");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://hd.stheadline.com/news/realtime/hk/1840833/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E-%E7%BE%8E%E5%9C%8B%E5%88%B6%E8%A3%81-%E7%89%B9%E5%8D%80%E6%94%BF%E5%BA%9C%E5%9A%B4%E6%89%B9%E5%8E%9A%E9%A1%8F%E7%84%A1%E6%81%A5%E5%8F%8A%E5%8D%91%E9%84%99-%E8%AD%B4%E8%B2%AC%E5%85%AC%E9%96%8B%E5%AE%98%E5%93%A1%E5%80%8B%E4%BA%BA%E8%B3%87%E6%96%99");

            final Item updatedItem = new HeadlineRealtimeParser("頭條即時", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("美國財政部宣布制裁11名中港官員，包括特首林鄭月娥、中聯辦主任駱惠寧等。特區政府發言人嚴厲批評「所謂", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("特區政府嚴厲批評「所謂」制裁。資料圖片", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://image2.stheadline.com/f/1500p0/0x0/100/hd/46e339a990456c63d6add431d0cdce78/stheadline/inewsmedia/20200808/_2020080812521731954.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

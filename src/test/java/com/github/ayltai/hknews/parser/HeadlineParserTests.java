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

public final class HeadlineParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hd.stheadline.com/news/daily/hk/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E/");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new HeadlineParser("頭條日報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(10, items.size(), "Incorrect item count");
            Assertions.assertEquals("\uFEFF全民免費檢測 最快兩周後展開", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://hd.stheadline.com/news/daily/hk/874104/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E-%E5%85%A8%E6%B0%91%E5%85%8D%E8%B2%BB%E6%AA%A2%E6%B8%AC-%E6%9C%80%E5%BF%AB%E5%85%A9%E5%91%A8%E5%BE%8C%E5%B1%95%E9%96%8B");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://hd.stheadline.com/news/daily/hk/874104/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E-%E5%85%A8%E6%B0%91%E5%85%8D%E8%B2%BB%E6%AA%A2%E6%B8%AC-%E6%9C%80%E5%BF%AB%E5%85%A9%E5%91%A8%E5%BE%8C%E5%B1%95%E9%96%8B");

            final Item updatedItem = new HeadlineParser("頭條日報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第三", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(2, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("檢測流程將電子化，毋須如現時般須排隊。\n資料圖片", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://static.stheadline.com/stheadline/news_res/2020/08/08/637017/wnnp001p01a.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

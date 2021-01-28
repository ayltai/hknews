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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import retrofit2.Call;
import retrofit2.Response;

public final class HkejParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hkej_list.html"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml(ArgumentMatchers.anyString());
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new HkejParser("信報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(9 + 9, items.size(), "Incorrect item count");
            Assertions.assertEquals("內地客經港轉機恢復 空服員憂播毒", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hkej_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://www1.hkej.com/dailynews/views/article/2551793/%E5%85%A7%E5%9C%B0%E5%AE%A2%E7%B6%93%E6%B8%AF%E8%BD%89%E6%A9%9F%E6%81%A2%E5%BE%A9+%E7%A9%BA%E6%9C%8D%E5%93%A1%E6%86%82%E6%92%AD%E6%AF%92");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://www1.hkej.com/dailynews/views/article/2551793/%E5%85%A7%E5%9C%B0%E5%AE%A2%E7%B6%93%E6%B8%AF%E8%BD%89%E6%A9%9F%E6%81%A2%E5%BE%A9+%E7%A9%BA%E6%9C%8D%E5%93%A1%E6%86%82%E6%92%AD%E6%AF%92");

            final Item updatedItem = new HkejParser("信報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("本港新冠肺炎疫情反覆，昨日新增69宗確診。機管局宣布機場明天（15日）起，將恢復內地旅客來港轉機/過", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("機場人來人往，旅客和工作人員宜做足保護措施。（中通社圖片）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://static.hkej.com/hkej/images/2020/08/14/2551793_ec376bc789a57a7a7869eb3b7b1c9e9e.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

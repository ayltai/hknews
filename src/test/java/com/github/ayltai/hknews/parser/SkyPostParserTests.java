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

public final class SkyPostParserTests extends ParserTests {
    @Test
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/skypost_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://skypost.ulifestyle.com.hk/sras001");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new SkyPostParser("晴報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(29, items.size(), "Incorrect item count");
            Assertions.assertEquals("外傭宿舍恐播毒將全作檢測 再多1傭工中招 勞工處正收集名單", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/skypost_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://skypost.ulifestyle.com.hk/article/2717383/%E5%A4%96%E5%82%AD%E5%AE%BF%E8%88%8D%E6%81%90%E6%92%AD%E6%AF%92%E5%B0%87%E5%85%A8%E4%BD%9C%E6%AA%A2%E6%B8%AC%20%E5%86%8D%E5%A4%9A1%E5%82%AD%E5%B7%A5%E4%B8%AD%E6%8B%9B%20%E5%8B%9E%E5%B7%A5%E8%99%95%E6%AD%A3%E6%94%B6%E9%9B%86%E5%90%8D%E5%96%AE");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://skypost.ulifestyle.com.hk/article/2717383/%E5%A4%96%E5%82%AD%E5%AE%BF%E8%88%8D%E6%81%90%E6%92%AD%E6%AF%92%E5%B0%87%E5%85%A8%E4%BD%9C%E6%AA%A2%E6%B8%AC%20%E5%86%8D%E5%A4%9A1%E5%82%AD%E5%B7%A5%E4%B8%AD%E6%8B%9B%20%E5%8B%9E%E5%B7%A5%E8%99%95%E6%AD%A3%E6%94%B6%E9%9B%86%E5%90%8D%E5%96%AE");

            final Item updatedItem = new SkyPostParser("晴報", this.sourceService, factory).updateItem(item);

            Assertions.assertEquals("本港確診個案再爬升，昨新增95宗確診，險觸三位數臨界點，並再有印傭入住宿舍後初步確診，而本周一確診的", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(4, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("<span>▲</span>昨多1印傭初步確診，她曾住上環「安盛僱傭中心」宿舍。（梁偉榮攝）", updatedItem.getImages().get(1).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://resource01-proxy.ulifestyle.com.hk/res/v3/image/content/2715000/2717383/20200807JAA005__20200807_S.jpg", updatedItem.getImages().get(1).getUrl(), "Incorrect image URL");
        }
    }
}

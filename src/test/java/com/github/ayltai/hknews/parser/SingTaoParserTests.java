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
import org.mockito.Mockito;

public final class SingTaoParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singtao_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://std.stheadline.com/daily/hongkong/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E");
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singtao_realtime_list.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://std.stheadline.com/realtime/hongkong/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E");
        }

        final Collection<Item> items = new SingTaoParser("星島日報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).getItems("港聞");

        Assertions.assertEquals(30 + 20, items.size(), "Incorrect item count");
        Assertions.assertEquals("社區隱性患者料逾千人", items.iterator().next().getTitle(), "Incorrect item description");
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentService service = Mockito.mock(ContentService.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singtao_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://std.stheadline.com/daily/article/2264439/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E-%E9%BB%8E%E6%99%BA%E8%8B%B1-%E5%B0%B1%E7%AE%97%E5%9D%90%E7%9B%A3%E4%BA%A6%E6%8F%80%E5%91%A2%E6%A2%9D%E8%B7%AF");

            final Item item = new Item();
            item.setUrl("https://std.stheadline.com/daily/article/2264439/%E6%97%A5%E5%A0%B1-%E6%B8%AF%E8%81%9E-%E9%BB%8E%E6%99%BA%E8%8B%B1-%E5%B0%B1%E7%AE%97%E5%9D%90%E7%9B%A3%E4%BA%A6%E6%8F%80%E5%91%A2%E6%A2%9D%E8%B7%AF");

            final Item updatedItem = new SingTaoParser("星島日報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("黎智英獲保釋候查，昨早由何文田寓所返回將軍澳壹傳媒大樓，其間向下屬表示，對未遭押返內地感到慶幸，但強", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("黎智英（右）昨早返回壹傳媒大樓。", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://image.stheadline.com/f/1500p0/0x0/100/none/785c3fa74a96c7257e7cfb96055d7dba/stheadline/news_res/2020/08/13/646188/i_src_332480307.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singtao_realtime_details.html"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            Mockito.doReturn(bufferedReader.lines().collect(Collectors.joining("\n"))).when(service).getHtml("https://std.stheadline.com/realtime/article/1340377/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E-%E6%9C%AC%E6%B8%AF%E5%A2%9E89%E5%AE%97%E7%A2%BA%E8%A8%BA32%E5%AE%97%E6%BA%90%E9%A0%AD%E4%B8%8D%E6%98%8E-%E7%9B%B4%E9%8A%B7%E5%85%AC%E5%8F%B8%E7%BE%A4%E7%B5%84%E5%86%8D%E5%A4%9A5%E5%AE%97");

            final Item item = new Item();
            item.setUrl("https://std.stheadline.com/realtime/article/1340377/%E5%8D%B3%E6%99%82-%E6%B8%AF%E8%81%9E-%E6%9C%AC%E6%B8%AF%E5%A2%9E89%E5%AE%97%E7%A2%BA%E8%A8%BA32%E5%AE%97%E6%BA%90%E9%A0%AD%E4%B8%8D%E6%98%8E-%E7%9B%B4%E9%8A%B7%E5%85%AC%E5%8F%B8%E7%BE%A4%E7%B5%84%E5%86%8D%E5%A4%9A5%E5%AE%97");

            final Item updatedItem = new SingTaoParser("星島日報", this.sourceService, service, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("衞生署衞生防護中心傳染病處主任張竹君公布，本港今日新增89宗確診個案。在確診個案中，8宗為輸入個案，", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("資料圖片", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://image.stheadline.com/f/1500p0/0x0/100/st/629b3ec9e7547d1ca769907d127a3c49/stheadline/inewsmedia/20200807/_2020080716424135741.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

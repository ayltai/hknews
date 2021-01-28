package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
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

public final class RthkParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws Exception {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/rthk.xml"), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final Call              call     = Mockito.mock(Call.class);
            final Response<RssFeed> response = Response.success(new Persister().read(RssFeed.class, bufferedReader.lines().collect(Collectors.joining("\n"))));

            Mockito.doReturn(call).when(service).getFeed("https://rthk.hk/rthk/news/rss/c_expressnews_clocal.xml");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new RthkParser("香港電台", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(20, items.size(), "Incorrect item count");
            Assertions.assertEquals("Facebook稱將禁遭美制裁中港官員帳戶支付服務", items.iterator().next().getTitle(), "Incorrect item title");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/rthk.htm"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("https://news.rthk.hk/rthk/ch/component/k2/1542567-20200808.htm");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("https://news.rthk.hk/rthk/ch/component/k2/1542567-20200808.htm");

            final Item updatedItem = new RthkParser("香港電台", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("美國宣布制裁11名香港與內地官員及前官員。其中的保安局局長李家超回應說，美國本身有大量維護國家安全的", updatedItem.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("李家超表示，維護國家安全是天公地義的事，美國想用所謂制裁作恫嚇，不會得逞。（港台圖片）", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://newsstatic.rthk.hk/images/mfile_1542567_1_20200808150805.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

public final class WenWeiPoParserTests extends ParserTests {
    @Test
    @Override
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/wenweipo_list.html"), Charset.forName("Big5"));
             BufferedReader bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("http://news.wenweipo.com/list_news.php?cat=000IN&instantCat=hk");
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new WenWeiPoParser("文匯報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).getItems("港聞");

            Assertions.assertEquals(29, items.size(), "Incorrect image count");
            Assertions.assertEquals("&#24314;&#35373;&#21147;&#37327;&#32879;&#21512;&#32882;&#26126; &#37197;&#21512;&#25919;&#24220;&#26045;&#25919;&#25884;&#25163;&#21109;&#26126;&#22825;", items.iterator().next().getTitle(), "Incorrect item description");
        }
    }

    @Test
    @Override
    public void testUpdateItem() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/wenweipo_details.htm"), Charset.forName("Big5"));
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml("http://news.wenweipo.com/2020/08/09/IN2008090047.htm");
            Mockito.doReturn(response).when(call).execute();

            final Item item = new Item();
            item.setUrl("http://news.wenweipo.com/2020/08/09/IN2008090047.htm");

            final Item updatedItem = new WenWeiPoParser("文匯報", this.sourceService, factory, Mockito.mock(LambdaLogger.class)).updateItem(item);

            Assertions.assertEquals("【文匯網訊】&#22823;&#20844;&#25991;&#21295;&#20840;&#23186;&#39636;&#22577;&#36947;：<span >&#24314;&#35373;&#21147;&#37327;&#26044;8&#26376;9&#26085;&#30332;&#34920;&#38988;&#28858;「&#39321;&#28207;&#35201;&#35722;&#38761; &#25884;&#25163;&#21109;&#26126;&#22825;」&#30340;&#32879;&#21512;&#32882;&#26126;，&#25552;&#20986;「&#20154;&#21629;&#38364;&#22825;，&#25239;&#30123;&#20778;&#20808;」、「&#24674;&#24489;&#32147;&#28639;，&#32019;&#35299;&#27665;&#22256;」、「&#25512;&#21205;&#35722;&#38761;，&#31361;&#30772;&#22256;&#23616;」&#21450;「&#25918;&#19979;&#27495;&#35211;，&#23432;&#35703;&#23478;&#22290;」&#22235;&#38917;&#20849;&#35672;，&#34920;&#31034;&#23559;&#20840;&#21147;&#37197;&#21512;&#25919;&#24220;&#26045;&#25919;，&#33287;&#20840;&#28207;&#24066;&#27665;&#40778;&#24515;&#25884;&#25163;&#25033;&#23565;&#30123;&#24773;，&#30332;&#23637;&#32147;&#28639;，&#20849;&#28193;&#38627;&#38364;。&#39321;&#28207;&#20877;&#20986;&#30332;&#22823;&#32879;&#30431;、&#27665;&#24314;&#32879;、&#24037;&#32879;&#26371;、&#26032;&#27665;&#40680;、&#33258;&#30001;&#40680;、&#32147;&#27665;&#32879;&#31561;42&#20491;&#22296;&#39636;&#21443;&#21152;&#32879;&#21512;&#32882;&#26126;。</span>", updatedItem.getDescription(), "Incorrect item description");
            Assertions.assertEquals(1, updatedItem.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("", updatedItem.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("http://assets.wenweipo.com/image/2020/08/09/wenwubiao_6c8dda88e69bd516339ea64fff43cb2a.jpg", updatedItem.getImages().get(0).getUrl(), "Incorrect image URL");
        }
    }
}

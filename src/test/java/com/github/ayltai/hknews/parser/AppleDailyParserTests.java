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

public final class AppleDailyParserTests extends ParserTests {
    @Test
    public void testGetItems() throws IOException {
        final ContentServiceFactory factory = Mockito.mock(ContentServiceFactory.class);
        final ContentService        service = Mockito.mock(ContentService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/appledaily.json"), StandardCharsets.UTF_8);
             BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader)) {
            final Call             call     = Mockito.mock(Call.class);
            final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

            Mockito.doReturn(call).when(service).getHtml(ArgumentMatchers.anyString());
            Mockito.doReturn(response).when(call).execute();

            final Collection<Item> items = new AppleDailyParser("蘋果日報", this.sourceService, factory).getItems("港聞");

            Assertions.assertEquals(200, items.size(), "Incorrect item count");

            final Item item = items.iterator().next();

            Assertions.assertEquals("美斥打壓民主　<br/>制裁林鄭　鄧炳強　11官", item.getTitle(), "Incorrect item title");
            Assertions.assertEquals("【本報訊】香港政府落實港版國安法後加強打壓港人自由，包括延遲立法會選舉及取消多名民主派人士參選資格，", item.getDescription().substring(0, 50), "Incorrect item description");
            Assertions.assertEquals(5, item.getImages().size(), "Incorrect image count");
            Assertions.assertEquals("美國財政部列出11名被制裁香港或中共官員名單，網址：https://home.treasury.gov/news/press-releases/sm1088。", item.getImages().get(0).getDescription(), "Incorrect image description");
            Assertions.assertEquals("https://cloudfront-ap-northeast-1.images.arcpublishing.com/appledaily/67FLDQ62IU46RWZVEFY36XIPWU.jpg", item.getImages().get(0).getUrl(), "Incorrect image URL");
            Assertions.assertEquals(1, item.getVideos().size(), "Incorrect video count");
            Assertions.assertEquals("https://d2i91erehhsxi2.cloudfront.net/appledaily/2020/08/07/5f2da5dac9e77c0007f8a51c/20200807_int_03_nAD_w.mp4", item.getVideos().get(0).getUrl(), "Incorrect video URL");
            Assertions.assertEquals("https://d87urpdhi5rdo.cloudfront.net/08-07-2020/t_06b7c281c7aa4ababbf911eda144d34c_name_1596825337_0d56.jpg", item.getVideos().get(0).getCover(), "Incorrect video thumb URL");
        }
    }
}

package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultParserFactory implements ParserFactory {
    //region Constants

    public static final String SOURCE_APPLE_DAILY             = "蘋果日報";
    public static final String SOURCE_HEADLINE                = "頭條日報";
    public static final String SOURCE_HEADLINE_REALTIME       = "頭條即時";
    public static final String SOURCE_HKEJ                    = "信報";
    public static final String SOURCE_HKEJ_REALTIME           = "信報即時";
    public static final String SOURCE_HKET                    = "經濟日報";
    public static final String SOURCE_MING_PAO                = "明報";
    public static final String SOURCE_ORIENTAL_DAILY          = "東方日報";
    public static final String SOURCE_ORIENTAL_DAILY_REALTIME = "東方即時";
    public static final String SOURCE_RTHK                    = "香港電台";
    public static final String SOURCE_SCMP                    = "南華早報";
    public static final String SOURCE_SING_PAO                = "成報";
    public static final String SOURCE_SING_TAO                = "星島日報";
    public static final String SOURCE_SKYPOST                 = "晴報";
    public static final String SOURCE_THE_STANDARD            = "英文虎報";
    public static final String SOURCE_WEN_WEI_PO              = "文匯報";

    //endregion

    private final SourceService         sourceService;
    private final ContentServiceFactory contentServiceFactory;

    @SuppressWarnings("checkstyle:cyclomaticComplexity")
    @NotNull
    @Override
    public Parser create(@NotNull final String sourceName) {
        switch (sourceName) {
            case DefaultParserFactory.SOURCE_APPLE_DAILY:
                return new AppleDailyParser(DefaultParserFactory.SOURCE_APPLE_DAILY, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_HEADLINE:
                return new HeadlineParser(DefaultParserFactory.SOURCE_HEADLINE, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_HEADLINE_REALTIME:
                return new HeadlineRealtimeParser(DefaultParserFactory.SOURCE_HEADLINE_REALTIME, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_HKEJ:
                return new HkejParser(DefaultParserFactory.SOURCE_HKEJ, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_HKEJ_REALTIME:
                return new HkejRealtimeParser(DefaultParserFactory.SOURCE_HKEJ_REALTIME, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_HKET:
                return new HketParser(DefaultParserFactory.SOURCE_HKET, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_MING_PAO:
                return new MingPaoParser(DefaultParserFactory.SOURCE_MING_PAO, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_ORIENTAL_DAILY:
                return new OrientalDailyParser(DefaultParserFactory.SOURCE_ORIENTAL_DAILY, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_ORIENTAL_DAILY_REALTIME:
                return new OrientalDailyRealtimeParser(DefaultParserFactory.SOURCE_ORIENTAL_DAILY_REALTIME, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_RTHK:
                return new RthkParser(DefaultParserFactory.SOURCE_RTHK, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_SCMP:
                return new ScmpParser(DefaultParserFactory.SOURCE_SCMP, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_SING_PAO:
                return new SingPaoParser(DefaultParserFactory.SOURCE_SING_PAO, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_SING_TAO:
                return new SingTaoParser(DefaultParserFactory.SOURCE_SING_TAO, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_SKYPOST:
                return new SkyPostParser(DefaultParserFactory.SOURCE_SKYPOST, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_THE_STANDARD:
                return new TheStandardParser(DefaultParserFactory.SOURCE_THE_STANDARD, this.sourceService, this.contentServiceFactory);

            case DefaultParserFactory.SOURCE_WEN_WEI_PO:
                return new WenWeiPoParser(DefaultParserFactory.SOURCE_WEN_WEI_PO, this.sourceService, this.contentServiceFactory);

            default:
                throw new IllegalArgumentException("Unrecognized source " + sourceName);
        }
    }
}

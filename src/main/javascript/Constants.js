export const Constants = {};

Constants.IS_DEBUG     = process.env.NODE_ENV !== 'production';
Constants.PROJECT_URL  = process.env.REACT_APP_PROJECT_URL || 'https://github.com/ayltai/hknews';
Constants.API_ENDPOINT = process.env.REACT_APP_API_ENDPOINT || 'http://localhost:3001';
Constants.APP_NAME     = process.env.REACT_APP_NAME || 'HK News';

Constants.PALETTE = {
    primary   : {
        main : '#8bc34a',
    },
    secondary : {
        main : '#ff9100',
    },
    error     : {
        main : '#f44336',
    },
    type      : 'light',
};

Constants.FETCH_DAYS            = 2;
Constants.NEWS_PER_PAGE         = 10;
Constants.NEWS_PER_PAGE_OPTIONS = [ 10, 25, 50, 100, ];
Constants.AVATAR_SIZE           = 40;
Constants.VIDEO_SIZE            = 320;

Constants.SOURCES = [
    '蘋果日報',
    '東方日報',
    '東方即時',
    '星島日報',
    '經濟日報',
    '成報',
    '明報',
    '頭條日報',
    '頭條即時',
    '晴報',
    '信報',
    '香港電台',
    '南華早報',
    '英文虎報',
    '文匯報',
];

Constants.SOURCE_IMAGES = {
    '蘋果日報' : 'appledaily',
    '東方日報' : 'orientaldaily',
    '東方即時' : 'orientaldaily',
    '星島日報' : 'singtao',
    '經濟日報' : 'hket',
    '成報'   : 'singpao',
    '明報'   : 'mingpao',
    '頭條日報' : 'headline',
    '頭條即時' : 'headline',
    '晴報'   : 'skypost',
    '信報'   : 'hkej',
    '信報即時' : 'hkej',
    '香港電台' : 'rthk',
    '南華早報' : 'scmp',
    '英文虎報' : 'thestandard',
    '文匯報'  : 'wenweipo',
};

Constants.CATEGORIES = {
    港聞 : require('@material-ui/icons/Home').default,
    國際 : require('@material-ui/icons/Language').default,
    兩岸 : require('@material-ui/icons/Security').default,
    經濟 : require('@material-ui/icons/MonetizationOn').default,
    地產 : require('@material-ui/icons/LocationCity').default,
    娛樂 : require('@material-ui/icons/SentimentVerySatisfied').default,
    副刊 : require('@material-ui/icons/VideogameAsset').default,
    體育 : require('@material-ui/icons/Rowing').default,
};

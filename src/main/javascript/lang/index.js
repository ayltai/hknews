import chineseMessages from 'ra-language-chinese-traditional';
import englishMessages from 'ra-language-english';

export const lang = {
    chinese : {
        ...chineseMessages,
        labels    : {
            read_more : '更多',
            publisher : '新聞出版商',
        },
        resources : {
            港聞 : {
                name : '港聞',
            },
            國際 : {
                name : '國際',
            },
            兩岸 : {
                name : '兩岸',
            },
            經濟 : {
                name : '經濟',
            },
            地產 : {
                name : '地產',
            },
            娛樂 : {
                name : '娛樂',
            },
            副刊 : {
                name : '副刊',
            },
            體育 : {
                name : '體育',
            },
        },
    },
    english : {
        ...englishMessages,
        labels    : {
            read_more : 'Read more',
            publisher : 'Publisher',
        },
        resources : {
            港聞 : {
                name : 'Local',
            },
            國際 : {
                name : 'World',
            },
            兩岸 : {
                name : 'China',
            },
            經濟 : {
                name : 'Economy',
            },
            地產 : {
                name : 'Property',
            },
            娛樂 : {
                name : 'Entertainment',
            },
            副刊 : {
                name : 'Supplement',
            },
            體育 : {
                name : 'Sports',
            },
        },
    },
};

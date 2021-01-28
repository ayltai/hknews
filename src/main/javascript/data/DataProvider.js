import { fetchUtils, } from 'react-admin';

import { StringUtils, } from '../util/StringUtils';
import { Constants, } from '../Constants';

const getPage = params => params && params.pagination ? params.pagination.page : 0;

const getSize = params => params && params.pagination ? params.pagination.perPage : Constants.NEWS_PER_PAGE;

const transformResponse = response => {
    return Object.prototype.hasOwnProperty.call(response.json, 'totalElements') ? {
        data  : response.json.content.map(transformItem),
        total : parseInt(response.json.totalElements, 10),
    } : {
        data : response.json,
    };
};

const transformItem = item => {
    if (Object.prototype.hasOwnProperty.call(item, 'title')) item.title = StringUtils.decode(item.title);
    if (Object.prototype.hasOwnProperty.call(item, 'uid')) item.id = item.uid;

    return item;
};

export const dataProvider = {
    getList: (resource, params) => {
        if (resource === 'sources') return fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/sources`).then(response => {
            return {
                data  : [...new Set(response.json.map(source => source.replace('信報即時', '信報').replace('即時', '日報')))]
                    .sort()
                    .map(source => ({
                        id   : source,
                        name : source,
                    })),
                total : response.json.length,
            };
        });

        return fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/items/${params && params.filter && params.filter.source ? `${params.filter.source},${params.filter.source.substr(0, 2)}即時` : Constants.SOURCES.join(',')}/${resource}/${Constants.FETCH_DAYS}?pageNumber=${getPage(params)}&pageSize=${getSize(params)}`).then(transformResponse);
    },

    getMany: (resource, params) => dataProvider.getList(resource, params),

    getManyReference: (resource, params) => dataProvider.getList(resource, params),

    getOne: (resource, params) => fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/item/${params.id}`).then(response => ({
        data : transformItem(response.json),
    })),
};

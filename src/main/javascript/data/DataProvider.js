import { fetchUtils, } from 'react-admin';

import { StringUtils, } from '../util/StringUtils';
import { Constants, } from '../Constants';

const getPage = params => params && params.pagination ? params.pagination.page - 1 : 0;

const getSize = params => params && params.pagination ? params.pagination.perPage : Constants.PAGE_SIZE;

const transform = response => Object.prototype.hasOwnProperty.call(response.json, 'totalElements') ? {
    data  : response.json.content,
    total : parseInt(response.json.totalElements, 10),
} : {
    data : response.json,
};

export const dataProvider = {
    getList: (resource, params) => {
        if (resource === 'sources') return fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/sources`).then(response => {
            return {
                data  : response.json.map(source => ({
                    id   : source,
                    name : source,
                })),
                total : response.json.length,
            };
        });

        return fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/items/${params && params.filter && params.filter.source ? `${params.filter.source},${params.filter.source.substr(0, 2)}即時` : Constants.SOURCES.join(',')}/${resource}/${Constants.FETCH_DAYS}?page=${getPage(params)}&size=${getSize(params)}`).then(transform);
    },

    getMany: (resource, params) => dataProvider.getList(resource, params),

    getManyReference: (resource, params) => dataProvider.getList(resource, params),

    getOne: (resource, params) => {
        return fetchUtils.fetchJson(`${Constants.API_ENDPOINT}/item/${params.id}`).then(response => {
            if (Object.prototype.hasOwnProperty.call(response.json, 'title')) response.json.title = StringUtils.decode(response.json.title);

            return {
                data : response.json,
            };
        });
    },
};

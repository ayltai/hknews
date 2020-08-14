import React from 'react';
import { List, Pagination, } from 'react-admin';

import { Constants, } from '../../Constants';
import { ScrollToTopOnMount, } from '../ScrollToTopOnMount';
import { ItemFilter, } from './ItemFilter';
import { ItemGrid, } from './ItemGrid';

export const ItemList = props => (
    <React.Fragment>
        <ScrollToTopOnMount />
        <List
            bulkActionButtons={false}
            exporter={false}
            filters={<ItemFilter />}
            perPage={Constants.NEWS_PER_PAGE}
            pagination={<Pagination rowsPerPageOptions={Constants.NEWS_PER_PAGE_OPTIONS} />}
            {...props}>
            <ItemGrid />
        </List>
    </React.Fragment>
);

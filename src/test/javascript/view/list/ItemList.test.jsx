import { createMuiTheme, MuiThemeProvider, } from '@material-ui/core';
import { mount, render, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'ra-test';

import { ItemList, } from '../../../../main/javascript/view/list/ItemList';

const component = (
    <TestContext
        initialState={{
            admin     : {
                resources : {
                    sources : {
                        data : {
                            id1 : {
                                title       : '',
                                description : '',
                                images      : [],
                                publishDate : '',
                                sourceName  : '',
                            },
                        },
                        list : {
                            ids         : [
                                'id1',
                            ],
                            params      : {},
                            selectedIds : [],
                            total       : 0,
                        },
                    },
                },
            },
        }}>
        <MuiThemeProvider theme={createMuiTheme({
            palette : {
                type : 'dark',
            },
        })}>
            <ItemList
                basePath='/'
                ids={[]}
                location={{
                    pathname : '',
                }}
                resource='source'>
                <div />
            </ItemList>
        </MuiThemeProvider>
    </TestContext>
);

describe('<ItemList />', () => {
    it('renders without errors', () => {
        expect(mount(component)
            .html()
            .replace(/id="mui-[0-9]*"/g, '')
            .replace(/aria-labelledby="(mui-[0-9]* *)*"/g, ''))
            .toMatchSnapshot();
    });

    it('mounts without errors', () => {
        render(component);
    });
});

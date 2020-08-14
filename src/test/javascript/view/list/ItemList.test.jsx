import { createMuiTheme, MuiThemeProvider, } from '@material-ui/core';
import { render, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'react-admin';
import renderer from 'react-test-renderer';

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
        expect(renderer.create(component).toJSON()).toMatchSnapshot();
    });

    it('mounts without errors', () => {
        render(component);
    });
});

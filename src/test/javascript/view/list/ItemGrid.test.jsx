import { createMuiTheme, MuiThemeProvider, } from '@material-ui/core';
import { render, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'ra-test';
import renderer from 'react-test-renderer';

import { ItemGrid, } from '../../../../main/javascript/view/list/ItemGrid';

const component = (
    <TestContext>
        <MuiThemeProvider theme={createMuiTheme({
            palette : {
                type : 'dark',
            },
        })}>
            <ItemGrid
                basePath='/'
                data={{
                    id1 : {
                        description : '',
                        images      : [
                            {
                                description : '',
                                imageUrl    : '',
                            },
                        ],
                        publishDate : '',
                        sourceName  : '',
                        title       : '',
                    },
                    id2 : {
                        description : '',
                        images      : [
                            {
                                description : '',
                                imageUrl    : '',
                            },
                        ],
                        publishDate : '',
                        sourceName  : '',
                        title       : '',
                    },
                }}
                ids={[
                    'id1',
                    'id2',
                ]} />
        </MuiThemeProvider>
    </TestContext>
);

describe('<ItemGrid />', () => {
    it('renders without errors', () => {
        expect(renderer.create(component).toJSON()).toMatchSnapshot();
    });

    it('mounts without errors', () => {
        render(component);
    });
});

import { mount, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'react-admin';
import { act, } from 'react-dom/test-utils';
import renderer from 'react-test-renderer';

import { ItemShow, } from '../../../../main/javascript/view/details/ItemShow';

describe('<ItemShow />', () => {
    const component = (
        <TestContext initialState={{
            admin : {
                customQueries : {},
            }
        }}>
            <ItemShow
                basePath='/'
                resource='foo' />
        </TestContext>
    );

    it('renders without errors', () => {
        expect(renderer.create(component).toJSON()).toMatchSnapshot();
    });

    it('mounts without errors', async () => {
        await act(async () => {
            mount(component);
        });
    });
});

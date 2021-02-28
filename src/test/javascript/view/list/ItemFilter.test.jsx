import { mount, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'ra-test';
import renderer from 'react-test-renderer';

import { ItemFilter, } from '../../../../main/javascript/view/list/ItemFilter';

const component = (
    <TestContext>
        <ItemFilter
            resource='source'
            displayFilters={{}}
            hideFilter={() => {
                return;
            }} />
    </TestContext>
);

describe('<ItemFilter />', () => {
    it('renders without errors', () => {
        expect(renderer.create(component).toJSON()).toMatchSnapshot();
    });

    it('mounts without errors', () => {
        mount(component);
    });
});

import { mount, } from 'enzyme';
import React from 'react';
import { TestContext, } from 'react-admin';
import renderer from 'react-test-renderer';

import { Gallery, } from '../../../../main/javascript/view/list/Gallery';

describe('<Gallery />', () => {
    const component = (
        <TestContext>
            <Gallery
                ids={[ 'id1', 'id2', ]}
                data={{
                    id1 : {
                        label  : '1',
                        source : 'image1',
                    },
                    id2 : {
                        label  : '2',
                        source : 'image2',
                    },
                }}
                label='label'
                source='source'>
                <img src='image1' />
            </Gallery>
        </TestContext>
    );

    it('renders without errors', () => {
        expect(renderer.create(component).toJSON()).toMatchSnapshot();
    });

    it('mounts without errors', () => {
        mount(component);
    });
});

import React from 'react';
import ReactDOM from 'react-dom';

import { App, } from '../../main/javascript/App';

describe('<App />', () => {
    it('renders without errors', () => {
        const div = document.createElement('div');

        ReactDOM.render(<App />, div);
        ReactDOM.unmountComponentAtNode(div);
    });
});

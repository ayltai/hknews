import PropTypes from 'prop-types';
import React from 'react';
import { ArrayField, ImageField, } from 'react-admin';

import { Gallery, } from '../list/Gallery';

export const Images = props => {
    if (props && props.record && props.record.images && props.record.images.length > 0) return (
        <ArrayField
            {...props}
            label=''
            source='images'>
            <Gallery
                caption='description'
                source='url'>
                <ImageField
                    label=''
                    source='url' />
            </Gallery>
        </ArrayField>
    );

    return <span />;
};

Images.propTypes = {
    record : PropTypes.object,
};

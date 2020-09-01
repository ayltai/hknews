import PropTypes from 'prop-types';
import React from 'react';
import { SingleFieldList, } from 'react-admin';
import Lightbox from 'react-image-lightbox';
import 'react-image-lightbox/style.css';

import { StringUtils, } from '../../util/StringUtils';

const getEntries = props => props.ids && props.data ? props.ids.map(id => props.data[id]) : [];

const getImages = props => Object.entries(getEntries(props)).map(record => ({
    label  : StringUtils.decode(record[1][props.caption]),
    source : record[1][props.source],
}));

export const Gallery = props => {
    const [ index, setIndex, ]      = React.useState(0);
    const [ open,  setOpen,  ]      = React.useState(false);
    const { source, ...otherProps } = props;
    const entries                   = getEntries(props);
    const images                    = getImages(props);

    const children = React.Children.map(props.children, child => React.cloneElement(child, {
        onClick : event => {
            setIndex(Math.max(0, entries ? entries.findIndex(entry => entry[source] === event.target.src) : 0));
            setOpen(true);
        },
    }));

    return (
        <React.Fragment>
            <SingleFieldList
                linkType={false}
                {...otherProps}>
                {children[0]}
            </SingleFieldList>
            {open && (
                <Lightbox
                    mainSrc={images[index].source}
                    nextSrc={index + 1 < images.length ? images[(index + 1) % images.length].source : undefined}
                    prevSrc={index - 1 >= 0 ? images[(index + images.length - 1) % images.length].source : undefined}
                    imageCaption={images[index].label}
                    onCloseRequest={() => setOpen(false)}
                    onMoveNextRequest={() => setIndex((index + 1) % images.length)}
                    onMovePrevRequest={() => setIndex((index + images.length - 1) % images.length)} />
            )}
        </React.Fragment>
    );
};

Gallery.propTypes = {
    ids      : PropTypes.arrayOf(PropTypes.number),
    data     : PropTypes.object,
    caption  : PropTypes.string,
    source   : PropTypes.string,
    children : PropTypes.object,
};

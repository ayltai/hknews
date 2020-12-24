import { CardMedia, makeStyles, } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';

export const LazyCardMedia = ({ className, component, image, }) => {
    const classes = makeStyles(theme => ({
        placeholder : {
            width  : theme.spacing(20),
            height : theme.spacing(20),
        },
    }))();

    const [ isVisible, setIsVisible, ] = React.useState(false);
    const placeholderRef               = React.useRef(null);

    React.useEffect(() => {
        if (!isVisible && placeholderRef.current) {
            const observer = new IntersectionObserver(([{ intersectionRatio, }]) => {
                if (intersectionRatio > 0) setIsVisible(true);
            });

            observer.observe(placeholderRef.current);

            return () => observer.disconnect();
        }
    }, [ isVisible, placeholderRef, ]);

    return (isVisible ?
        <CardMedia
            className={className}
            component={component || 'img'}
            image={image} /> :
        <div
            className={classes.placeholder}
            ref={placeholderRef} />
    );
};

LazyCardMedia.propTypes = {
    className : PropTypes.string,
    component : PropTypes.string,
    image     : PropTypes.string.isRequired,
};

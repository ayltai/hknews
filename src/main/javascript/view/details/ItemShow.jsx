import { makeStyles, } from '@material-ui/core';
import React from 'react';
import { ArrayField, DateField, ImageField, RichTextField, SimpleShowLayout, ShowController, ShowView, TextField, } from 'react-admin';

import { Gallery, } from '../list/Gallery';
import { ScrollToTopOnMount, } from '../ScrollToTopOnMount';

export const ItemShow = props => {
    const classes = makeStyles({
        title : {
            paddingBottom : '0.75em',
            fontSize      : '120%',
            fontWeight    : 'bold',
        },
    })();

    return (
        <React.Fragment>
            <ScrollToTopOnMount />
            <ShowController {...props}>
                {controllerProps => (
                    <ShowView
                        {...props}
                        {...controllerProps}>
                        <SimpleShowLayout>
                            <RichTextField
                                className={classes.title}
                                label=''
                                source='title' />
                            <RichTextField
                                label=''
                                source='description' />
                            {controllerProps && controllerProps.record && controllerProps.record.images && controllerProps.record.images.length > 0 && (
                                <ArrayField
                                    label=''
                                    source='images'>
                                    <Gallery
                                        label='description'
                                        source='imageUrl'>
                                        <ImageField
                                            label=''
                                            source='imageUrl' />
                                    </Gallery>
                                </ArrayField>
                            )}
                            <TextField
                                label=''
                                source='sourceName' />
                            <DateField
                                label=''
                                source='publishDate'
                                showTime />
                        </SimpleShowLayout>
                    </ShowView>
                )}
            </ShowController>
        </React.Fragment>
    );
};

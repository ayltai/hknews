import { makeStyles, } from '@material-ui/core';
import React from 'react';
import { DateField, RichTextField, SimpleShowLayout, ShowController, ShowView, TextField, } from 'react-admin';

import { ScrollToTopOnMount, } from '../ScrollToTopOnMount';
import { Images, } from './Images';
import { Videos, } from './Videos';

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
                            <Videos {...controllerProps} />
                            <Images {...controllerProps} />
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

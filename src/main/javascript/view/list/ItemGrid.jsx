import { Card, CardActions, CardActionArea, CardContent, CardHeader, CardMedia, Grid, makeStyles, Tooltip, } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { RichTextField, ShowButton, TextField, } from 'react-admin';
import TimeAgo from 'react-timeago';

import { StringUtils, } from '../../util/StringUtils';
import { Constants, } from '../../Constants';

export const ItemGrid = ({ basePath, ids, data, }) => {
    const classes = makeStyles({
        container   : {
            padding  : 16,
            flexGrow : 1,
        },
        title       : {
            paddingBottom : '0.75em',
            display       : 'block',
            overflow      : 'hidden',
            fontSize      : '120%',
            fontWeight    : 'bold',
            textOverflow  : 'ellipsis',
            whiteSpace    : 'nowrap',
        },
        description : {
            minHeight       : 200,
            display         : '-webkit-box',
            overflow        : 'hidden',
            textOverflow    : 'ellipsis',
            WebkitBoxOrient : 'vertical',
            WebkitLineClamp : 8,
        },
        media       : {
            height     : 0,
            paddingTop : '56.25%',
        },
        actions     : {
            display   : 'block',
            textAlign : 'right',
        },
    })();

    return (
        <div className={classes.container}>
            <Grid
                container
                spacing={2}>
                {ids.map(id => (
                    <Grid
                        item
                        key={id}
                        xs={12}
                        sm={6}
                        md={6}
                        lg={4}
                        xl={3}>
                        <Card>
                            <CardHeader
                                title={
                                    <TextField
                                        record={data[id]}
                                        source='sourceName' />
                                }
                                subheader={<TimeAgo date={data[id].publishDate} />}
                                avatar={
                                    <img
                                        src={`${Constants.API_ENDPOINT}/images/${Constants.SOURCE_IMAGES[data[id].sourceName]}.png`}
                                        width={Constants.AVATAR_SIZE}
                                        height={Constants.AVATAR_SIZE}
                                        title={data[id].sourceName}
                                        alt={data[id].sourceName} />
                                } />
                            <CardActionArea href={`#${basePath}/${id}/show`}>
                                {data[id].images && data[id].images.length > 0 && (
                                    <Tooltip title={data[id].images[0].description ? StringUtils.decode(data[id].images[0].description) : ''}>
                                        <CardMedia
                                            className={classes.media}
                                            image={data[id].images[0].imageUrl} />
                                    </Tooltip>
                                )}
                                <CardContent>
                                    <RichTextField
                                        className={classes.title}
                                        record={data[id]}
                                        source='title' />
                                    <RichTextField
                                        className={classes.description}
                                        record={data[id]}
                                        source='description' />
                                </CardContent>
                            </CardActionArea>
                            <CardActions className={classes.actions}>
                                <ShowButton
                                    basePath={basePath}
                                    label='labels.read_more'
                                    record={data[id]} />
                            </CardActions>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </div>
    );
};

ItemGrid.propTypes = {
    basePath : PropTypes.string,
    ids      : PropTypes.arrayOf(PropTypes.string),
    data     : PropTypes.object,
};

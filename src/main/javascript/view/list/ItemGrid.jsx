import { Card, CardActions, CardActionArea, CardContent, CardHeader, CardMedia, Grid, makeStyles, Tooltip, } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { RichTextField, ShowButton, TextField, } from 'react-admin';
import TimeAgo from 'react-timeago';

import { StringUtils, } from '../../util/StringUtils';
import { Constants, } from '../../Constants';

export const ItemGrid = ({ basePath, ids, data, }) => {
    const classes = makeStyles(theme => ({
        container   : {
            padding  : theme.spacing(2),
            flexGrow : 1,
        },
        card        : {
            display : 'flex',
        },
        details     : {
            display       : 'flex',
            flexDirection : 'column',
            flexGrow      : 1,
        },
        content     : {
            flex : '1 0 auto',
        },
        title       : {
            display         : '-webkit-box',
            overflow        : 'hidden',
            fontSize        : '120%',
            fontWeight      : 'bold',
            textOverflow    : 'ellipsis',
            whiteSpace      : 'break-spaces',
            WebkitBoxOrient : 'vertical',
            WebkitLineClamp : 1,
        },
        description : {
            minHeight       : theme.spacing(12),
            display         : '-webkit-box',
            overflow        : 'hidden',
            textOverflow    : 'ellipsis',
            WebkitBoxOrient : 'vertical',
            WebkitLineClamp : 4,
        },
        media       : {
            minWidth   : theme.spacing(20),
            width      : theme.spacing(20),
            minHeight  : theme.spacing(20),
            height     : theme.spacing(20),
            marginLeft : theme.spacing(2),
        },
        actions     : {
            display : 'block',
        },
    }))();

    return (
        <div className={classes.container}>
            <Grid
                container
                spacing={2}>
                {ids.map(id => (
                    <Grid
                        item
                        key={id}
                        lg={12}
                        xl={6}>
                        <Card>
                            <CardActionArea href={`#${basePath}/${id}/show`}>
                                <CardHeader
                                    className={classes.content}
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
                                <CardContent className={classes.content}>
                                    <div className={classes.card}>
                                        <div className={classes.details}>
                                            <RichTextField
                                                className={classes.title}
                                                record={data[id]}
                                                source='title' />
                                            <RichTextField
                                                className={classes.description}
                                                record={data[id]}
                                                source='description' />
                                        </div>
                                        {data[id].images && data[id].images.length > 0 && (
                                            <Tooltip title={data[id].images[0].description ? StringUtils.decode(data[id].images[0].description) : ''}>
                                                <CardMedia
                                                    className={classes.media}
                                                    image={data[id].images[0].url} />
                                            </Tooltip>
                                        )}
                                    </div>
                                </CardContent>
                                <CardActions className={classes.actions}>
                                    <ShowButton
                                        basePath={basePath}
                                        label='labels.read_more'
                                        record={data[id]} />
                                </CardActions>
                            </CardActionArea>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </div>
    );
};

ItemGrid.propTypes = {
    basePath : PropTypes.string,
    ids      : PropTypes.arrayOf(PropTypes.number),
    data     : PropTypes.object,
};

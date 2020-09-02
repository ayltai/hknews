import { makeStyles, } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { BigPlayButton, ControlBar, LoadingSpinner, Player, } from 'video-react';
import 'video-react/dist/video-react.css';

import { Constants, } from '../../Constants';

export const Videos = props => {
    const classes = makeStyles(theme => ({
        container : {
            display : 'flex',
        },
        video     : {
            margin : theme.spacing(1),
        },
    }))();

    if (props && props.record && props.record.videos && props.record.videos.length > 0) return (
        <div className={classes.container}>
            {props.record.videos.map(video => video.url.startsWith('https://www.youtube.com/') || video.url.startsWith('https://youtu.be/') ? (
                <iframe
                    key={video.id}
                    className={classes.video}
                    title={video.url}
                    frameBorder={0}
                    allowFullScreen={true}
                    width={Constants.VIDEO_SIZE}
                    height={Constants.VIDEO_SIZE / 16 * 9}
                    src={video.url} />
            ) : (
                <Player
                    key={video.id}
                    className={classes.video}
                    fluid={false}
                    width={Constants.VIDEO_SIZE}
                    poster={video.cover}
                    src={video.url}>
                    <LoadingSpinner />
                    <BigPlayButton position='center' />
                    <ControlBar autoHide={false} />
                </Player>
            ))}
        </div>
    );

    return <span />;
};

Videos.propTypes = {
    record : PropTypes.object,
};

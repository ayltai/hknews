import { makeStyles, } from '@material-ui/core';
import { FormControlLabel, Switch, } from '@material-ui/core';
import { Translate, } from '@material-ui/icons';
import React from 'react';
import { useLocale, useSetLocale, } from 'react-admin';

export const LocaleToggle = props => {
    const classes = makeStyles(theme => ({
        switch : {
            marginLeft  : theme.spacing(0),
            marginRight : theme.spacing(2),
            alignItems  : 'end',
        },
    }))();

    const locale    = useLocale();
    const setLocale = useSetLocale();

    return (
        <FormControlLabel
            className={classes.switch}
            {...props}
            label={<Translate />}
            control={
                <Switch
                    checked={locale === 'zh-TW'}
                    onChange={event => setLocale(event.target.checked ? 'zh-TW' : 'en')} />
            } />
    );
};

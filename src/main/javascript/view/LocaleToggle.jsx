import { FormControlLabel, makeStyles, Switch, } from '@material-ui/core';
import { Translate, } from '@material-ui/icons';
import React from 'react';
import { useSetLocale, } from 'react-admin';

import { Preferences, } from '../data/Preferences';

export const LocaleToggle = props => {
    const classes = makeStyles(theme => ({
        switch : {
            marginLeft  : theme.spacing(0),
            marginRight : theme.spacing(2),
            alignItems  : 'end',
        },
    }))();

    const preferences = Preferences.load();
    const setLocale   = useSetLocale();

    React.useEffect(() => {
        setLocale(preferences.locale).catch(console.error);
    }, [ preferences.locale, setLocale, ]);

    return (
        <FormControlLabel
            className={classes.switch}
            {...props}
            label={<Translate />}
            control={
                <Switch
                    checked={preferences.locale === 'zh-TW'}
                    onChange={async (event) => {
                        const newLocale = event.target.checked ? 'zh-TW' : 'en';

                        preferences.locale = newLocale;
                        preferences.save();

                        await setLocale(newLocale);
                    }} />
            } />
    );
};

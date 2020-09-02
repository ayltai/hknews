import { createMuiTheme, FormControlLabel, FormGroup, IconButton, makeStyles, MuiThemeProvider, SvgIcon, Switch, Tooltip, Typography, } from '@material-ui/core';
import { BrightnessMedium, } from '@material-ui/icons';
import polyglotI18nProvider from 'ra-i18n-polyglot';
import React from 'react';
import { Admin, AppBar, hideNotification, Layout, Notification, Resource, showNotification, } from 'react-admin';
import { Detector, } from 'react-detect-offline';

import { dataProvider, } from './data/DataProvider';
import { lang, } from './lang';
import { ItemShow, } from './view/details/ItemShow';
import { ItemList, } from './view/list/ItemList';
import { LocaleToggle, } from './view/LocaleToggle';
import { Constants, } from './Constants';

export const App = props => {
    const classes = makeStyles(theme => ({
        spacer : {
            flex : 1,
        },
        switch : {
            marginLeft  : theme.spacing(0),
            marginRight : theme.spacing(2),
            alignItems  : 'end',
        },
    }))();

    const palette                    = Constants.PALETTE;
    const [ darkMode, setDarkMode, ] = React.useState(false);
    const [ theme,    setTheme,    ] = React.useState({ palette, });
    const appTheme                   = createMuiTheme(theme);

    const toggleDarkMode = event => {
        palette.type = event.target.checked ? 'dark' : 'light';

        setDarkMode(event.target.checked);
        setTheme({ palette, });
    };

    return (
        <MuiThemeProvider theme={appTheme}>
            <Admin
                {...props}
                title={Constants.APP_NAME}
                authProvider={false}
                dataProvider={dataProvider}
                i18nProvider={polyglotI18nProvider(locale => locale && locale.substr(0, 2) === 'zh' ? lang.chinese : lang.english)}
                theme={appTheme}
                layout={layoutProps => (
                    <Layout
                        {...layoutProps}
                        appBar={appBarProps => (
                            <AppBar
                                {...appBarProps}
                                color='default'
                                userMenu={<span />}>
                                <Typography
                                    color='inherit'
                                    variant='h6'>
                                    {Constants.APP_NAME}
                                </Typography>
                                <span className={classes.spacer} />
                                <FormGroup row>
                                    <LocaleToggle />
                                    <FormControlLabel
                                        className={classes.switch}
                                        label={<BrightnessMedium />}
                                        control={
                                            <Switch
                                                checked={darkMode}
                                                onChange={toggleDarkMode} />
                                        } />
                                </FormGroup>
                                <Tooltip title='GitHub'>
                                    <IconButton onClick={() => window.open(Constants.PROJECT_URL, '_blank')}>
                                        <SvgIcon htmlColor={appTheme.palette.text.primary}>
                                            <path d='M12 .3a12 12 0 0 0-3.8 23.4c.6.1.8-.3.8-.6v-2c-3.3.7-4-1.6-4-1.6-.6-1.4-1.4-1.8-1.4-1.8-1-.7.1-.7.1-.7 1.2 0 1.9 1.2 1.9 1.2 1 1.8 2.8 1.3 3.5 1 0-.8.4-1.3.7-1.6-2.7-.3-5.5-1.3-5.5-6 0-1.2.5-2.3 1.3-3.1-.2-.4-.6-1.6 0-3.2 0 0 1-.3 3.4 1.2a11.5 11.5 0 0 1 6 0c2.3-1.5 3.3-1.2 3.3-1.2.6 1.6.2 2.8 0 3.2.9.8 1.3 1.9 1.3 3.2 0 4.6-2.8 5.6-5.5 5.9.5.4.9 1 .9 2.2v3.3c0 .3.1.7.8.6A12 12 0 0 0 12 .3' />
                                        </SvgIcon>
                                    </IconButton>
                                </Tooltip>
                            </AppBar>
                        )}
                        notification={notificationProps => (
                            <Notification
                                {...notificationProps}
                                autoHideDuration={60 * 60 * 1000} />
                        )} />
                )}>
                {Object.entries(Constants.CATEGORIES).map(entry => (
                    <Resource
                        key={entry[0]}
                        name={entry[0]}
                        icon={entry[1]}
                        list={ItemList}
                        show={ItemShow}
                        options={{
                            label : entry[0],
                        }} />
                ))}
                <Resource name='sources' />
            </Admin>
            <Detector
                polling={{
                    url : Constants.API_ENDPOINT,
                }}
                render={({ online, }) => {
                    if (online) {
                        showNotification('ra.notification.http_error', 'warning');
                    } else {
                        hideNotification();
                    }

                    return <span />;
                }} />
        </MuiThemeProvider>
    );
};

const KEY = 'preferences';

export class Preferences {
    locale   = 'zh-TW';
    darkMode = false;

    save = () => window.localStorage.setItem(KEY, JSON.stringify(this));
}

Preferences.load = () => KEY in window.localStorage ? Object.assign(new Preferences(), JSON.parse(window.localStorage.getItem(KEY))) : new Preferences();

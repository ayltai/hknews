import { useEffect, } from 'react';

export const ScrollToTopOnMount = () => {
    useEffect(() => {
        try {
            window.scroll({
                left     : 0,
                top      : 0,
                behavior : 'smooth',
            });
        } catch {
            window.scrollTo(0, 0);
        }
    }, []);

    return null;
};

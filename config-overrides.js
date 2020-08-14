const path = require('path');

module.exports = {
    paths : function(paths) {
        paths.appIndexJs = path.resolve(__dirname, 'src', 'main', 'javascript', 'index.jsx');
        paths.appBuild   = path.resolve(__dirname, 'build', 'javascript');

        return paths;
    },
};

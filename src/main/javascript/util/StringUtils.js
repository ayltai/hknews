export const StringUtils = {};

StringUtils.decode = input => input ? input.replace(/&#(\d+);/g, (match, code) => String.fromCharCode(code)) : input;

import { StringUtils, } from '../../../main/javascript/util/StringUtils';

it('decodes correctly', () => {
    expect(StringUtils.decode(undefined)).toBeUndefined();
    expect(StringUtils.decode('English')).toBe('English');
    expect(StringUtils.decode('&#38;')).toBe('&');
});

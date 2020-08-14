import { dataProvider, } from '../../../main/javascript/data/DataProvider';

it('calls Source API without errors', () => {
    expect(dataProvider.getList('sources').data).not.toBeNull();
});

it('calls Item API without errors', () => {
    expect(dataProvider.getList('港聞').data).not.toBeNull();
});

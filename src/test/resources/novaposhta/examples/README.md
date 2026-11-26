# Examples copied from the Nova Poshta documentation

Source: https://developers.novaposhta.ua/documentation. All 88 pages were read on 2026-11-26 (85 JSON method pages and 3 print pages; the print pages
have no JSON example). One file per JSON page, named by the first 8 characters of the page id (the same ids as `doc=` in `../api-spec.txt`):

* `<pageId>.request.json` - the `methodProperties` object of the page's JSON request example;
* `<pageId>.response.json` - the whole answer of the page's JSON response example.

Every one of the 85 JSON pages has both files. `DocsFixturesStrictTest` reads them all: an answer must deserialize with
`FAIL_ON_UNKNOWN_PROPERTIES` enabled and the class-level `ignoreUnknown` switched off (so a field the model does not declare fails the test; models
that keep unknown fields in an `extra` map must leave it empty), and a request must survive parse -> serialize unchanged. A request whose example
shows `"Limit": ""` and a model that leaves that parameter out count as the same (an empty string means "not set"). A method that the spec lists
without parameters must have an empty example.

## What was normalised

The portal's examples are not valid JSON as published. Only syntax was repaired; no field was added, removed or renamed except where a page is
named below. The repairs were made by a program that logs each one, and the result was compared with the 19 pages that had been normalised by hand
the day before: identical, apart from the choice of a replacement date and two answers that had been cut off then (`dfc923e9`, `a9ae7bc9`).

General (the examples look as if every comma were turned into `",` by whatever generated them):

* a stray quote after a value: `"Parcel":0",`, `"success":true",`, `"errors":[]",`, `}]",`, and a line that holds only `",` after a closing brace;
* a missing comma between two lines (almost every answer lacks the comma before `"errors"`);
* a doubled opening quote: `"Page" : ""1"`;
* unescaped quotes inside a value: `"... (маг. "Фора")"` (pages `a886b776`, `a9ae7bc9`, `c61d3597`) - escaped;
* the placeholders `дд.мм.рррр` and `дд.мм.рррр чч:хх:сс` in date fields - replaced by a real date of the same shape (`25.11.2026`, delivery dates
  `26.11.2026`; the pages normalised by hand keep the dates chosen then);
* the placeholder `[ВАШ КЛЮЧ]` is not stored (requests keep only `methodProperties`);
* three request values that are not dates in a date field (the models type such a field as `LocalDate` and write it as `dd.MM.yyyy`, so the
  round-trip test needs a real date): `09-18-16` (`a56d5c1c`, `DateTime`) and `12/10/15 10:33` (`a82d087c`, `BeginDate` and `EndDate`) became `25.11.2026`,
  the format the page's description gives ("дд.мм.рррр"). Answers are **not** normalised in this respect: the US style `4/2/21 0:00` (`Warehouse.WarehouseStatusDate`),
  `12/10/15 10:33` (`ReturnOrder.EstimatedDeliveryDate`) and the offset form `2015-03-20T13:45:19+00:00` (`ScanSheet.DateTime`) are read as they stand.

Single pages:

* `06b78d02` response: `"data": [{ {..}, {..} }]` (one pair of braces too many) is read as the two elements it obviously means;
* `a19ced79` request: `"SettlementType": "місто" або "м."` is reduced to its first alternative, `"місто"`;
* `a6492db4` request: the type name is glued to the key in the documentation (`Lengthstring`, `Widthstring`, `Heightstring`,
  `VolumetricWeightstring`, `TypeOfPackingstring`); the keys are `Length`, `Width`, `Height`, `VolumetricWeight`, `TypeOfPacking`, as in the same
  page's answer;
* `a9ae7bc9` response: the key `CounterpartySenderType` occurs twice in the example; the last value is kept;
* `0ae5dd75` response: the `ContactPerson` array holds a complete nested answer envelope (`success`, `data`, `errors`, ...). Kept as published.

## Things to know when repeating the capture

* The portal serves two versions of the page list in turn: 88 pages, or 84 without `06b78d02`, `1934683d`, `98acb0f6`, `c09f1b02` (all
  AdditionalService). The raw HTML of `/documentation` always carried the links; build the list from several responses.
* The parameter tables of three `delete` pages and of `06b78d02` are empty or placeholders ("NoName"); `getPackList` glues the type to the name in its
  table as well as in its example. The spec takes those fields from the examples.
* The real answer of the live API is not the documented one in every detail: `TrackingDocument/getStatusDocuments`
  returns 128 fields where the documentation shows 107, and `warnings` may contain objects. These fixtures are what the documentation says, not what the
  API does.

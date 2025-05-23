// Mock the act function from react-dom/test-utils with the one from react
// This fixes the warning: "Warning: `ReactDOMTestUtils.act` is deprecated in favor of `React.act`"
jest.mock('react-dom/test-utils', () => {
  const originalModule = jest.requireActual('react-dom/test-utils');
  const React = jest.requireActual('react');

  return {
    ...originalModule,
    act: React.act,
  };
});

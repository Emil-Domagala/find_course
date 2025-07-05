import '@testing-library/jest-dom';

global.ResizeObserver = class {
  observe() {}
  unobserve() {}
  disconnect() {}
};

if (!HTMLElement.prototype.hasPointerCapture) {
  HTMLElement.prototype.hasPointerCapture = () => false;
}
if (!window.HTMLElement.prototype.scrollIntoView) {
  window.HTMLElement.prototype.scrollIntoView = jest.fn();
}

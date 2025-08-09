// Import zone.js since it is no longer listed directly in angular.json
import 'zone.js';
import '@angular/localize/init';

// Add polyfill for 'global'
// This declaration line helps avoid the error `ReferenceError: global is not defined`
// when using libraries developed from Node.js such as SocketJS
(window as any).global = window;

// More polyfills may be added here in the future

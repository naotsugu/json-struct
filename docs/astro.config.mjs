import { defineConfig } from 'astro/config';
import preact from '@astrojs/preact';

export default defineConfig({
	integrations: [
		preact(),
	],
	site: `https://naotsugu.github.io`,
	base: '/json-struct',
});

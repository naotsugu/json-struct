export const SITE = {
	title: 'Json Struct',
	description: 'json-struct user guide.',
	defaultLanguage: 'en_US',
};

export const OPEN_GRAPH = {
	image: {
		src: 'https://github.com/naotsugu/json-struct/blob/main/docs/public/banner-minimal.png?raw=true',
		alt: 'Json Struct',
	}
};

// This is the type of the frontmatter you put in the docs markdown files.
export type Frontmatter = {
	title: string;
	description: string;
	layout: string;
	image?: { src: string; alt: string };
	dir?: 'ltr' | 'rtl';
	ogLocale?: string;
	lang?: string;
};

export const KNOWN_LANGUAGES = {
	English: 'en',
	Japanese: 'ja'
} as const;
export const KNOWN_LANGUAGE_CODES = Object.values(KNOWN_LANGUAGES);

export type Sidebar = Record<
	typeof KNOWN_LANGUAGE_CODES[number],
	Record<string, { text: string; link: string }[]>
>;
export const SIDEBAR: Sidebar = {
	en: {
		'Table of Contents': [
			{ text: 'User Guide', link: '../../en/guide' },
			{ text: 'Release notes', link: '../../en/release-notes' },
		]
	},
	ja: {
		'Table of Contents': [
			{ text: 'User Guide', link: '../../ja/guide' },
			{ text: 'Release notes', link: '../../ja/release-notes' },
		]
	},
};

import { defineConfig } from 'orval';


export default defineConfig({
    'OpenBadgesPlatform': {
        input: 'openapi.yaml',
        output: {
            mode: 'tags-split',
            target: 'app/services/OpenBadgesPlatformService.ts',
            schemas: 'app/models',
            client: 'fetch'
        }
    }
});
import tailwindcss from "@tailwindcss/vite";

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  ssr: false,
  devtools: { enabled: false },
  app: {
    head: {
      title: 'Open Badges Platform',
      link: [
        { rel: 'icon', type: 'image/svg', href: '/favicon.svg' }
      ]
    }
  },
  css: ['./app/assets/css/main.css'],
  vite: { plugins: [tailwindcss(),], },
  modules: ['@nuxt/icon', 'nuxt-toast']
})
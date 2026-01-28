import tailwindcss from "@tailwindcss/vite";

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  ssr: false,
  devtools: { enabled: false },
  css: ['./app/assets/css/main.css'],
  vite: { plugins: [tailwindcss(),], },
  modules: ['@nuxt/icon']
})
<script setup>
import { getApiAuthMe } from '~/services/authentication-resource/authentication-resource';

const { error, data } = useLazyAsyncData(() => getApiAuthMe())

watch(error, (error) => {
    if (error) return navigateTo('/api/auth/login', { external: true })
})

watch(data, (data) => {
    if (data.status == 302) return navigateTo('/api/auth/login', { external: true })
    if (data.status == 404) return navigateTo('/register')
    if (data.status == 200) useAuth().me.value = data.data
    if (data.data.account) return navigateTo('/')
})
</script>

<template>
    <div class="grid place-content-center h-screen">
        <span class="loading loading-ring loading-xl"></span>
    </div>
</template>
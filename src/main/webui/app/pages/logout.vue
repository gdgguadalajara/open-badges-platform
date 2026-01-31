<script setup>
import { getApiAuthMe } from '~/services/authentication-resource/authentication-resource';

const auth = useAuth()

const { error, data } = useLazyAsyncData(() => getApiAuthMe())

watch(error, (error) => {
    if (error) return navigateTo('/')
})
watch(data, (data) => {
    if (data.status == 200) return navigateTo('/api/auth/logout', { external: true })
})
</script>

<template>
    <div class="grid place-content-center h-screen">
        <span class="loading loading-ring loading-xl"></span>
    </div>
</template>
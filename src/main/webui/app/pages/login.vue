<script setup>
const { meResponse, fetchMe } = useAuth()

onMounted(() => {
    fetchMe()
})

watchEffect(() => {
    if (meResponse.value?.status && meResponse.value?.status >= 300 && meResponse.value?.status <= 399)
        return navigateTo('/api/auth/login', { external: true })
    if (meResponse.value?.status && meResponse.value?.status >= 400 && meResponse.value?.status <= 499)
        return navigateTo('/register')
})
</script>

<template>
    <div class="grid place-content-center h-screen">
        <span class="loading loading-ring loading-xl"></span>
    </div>
</template>
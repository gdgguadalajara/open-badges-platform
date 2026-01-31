<script setup>
const { me } = useAuth()
</script>

<template>
    <slot v-if="me?.account?.isSuperAdmin"></slot>
</template>
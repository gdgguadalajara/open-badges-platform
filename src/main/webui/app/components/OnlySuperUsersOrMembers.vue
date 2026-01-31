<script setup>
import { MemberRole } from '~/models'
const { issuerUuid, roles } = defineProps(['issuerUuid', 'roles'])
const { me } = useAuth()
</script>

<template>
    <slot v-if="
        me?.account?.isSuperAdmin
        || me?.memberships?.some(membership => membership.issuer.id === issuerUuid
            && (roles ?? Object.values(MemberRole)).includes(membership.role))">
    </slot>
</template>
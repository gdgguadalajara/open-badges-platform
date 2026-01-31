<script setup>
const { me } = useAuth()

onMounted(() => {
    if (me.value?.memberships.length === 1)
        navigateTo(`/organizations/${me.value.memberships[0].issuer.id}`)
})
</script>

<template>
    <div class="card bg-base-200 shadow-sm">
        <div class="card-body">
            <h1 class="card-title">Mis Organizaciones</h1>
            <div class="divider m-0"></div>
            <div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                <NuxtLink :to="`/organizations/${membership.issuer.id}`" v-for="membership in me?.memberships"
                    :key="membership.id" class="card bg-base-100 shadow hover:shadow-xl transition">
                    <div class="card-body items-center">
                        <h2 class="card-title text-lg">{{ membership.issuer.name }}</h2>
                        <img :src="membership.issuer.logoUrl" :alt="membership.issuer.name"
                            class="w-16 h-16 rounded-full mb-4 shadow" />
                    </div>
                </NuxtLink>
            </div>
        </div>
    </div>
</template>
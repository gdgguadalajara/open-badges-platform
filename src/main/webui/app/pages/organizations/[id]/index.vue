<script setup>
import { MemberRole } from '~/models'
import { getApiAdminIssuersUuid } from '~/services/admin-resource/admin-resource'

const toast = useToast()
const route = useRoute()
const organizationId = route.params.id

const jsonDl = ref({})

const { data: payload, status } = useLazyAsyncData(() => getApiAdminIssuersUuid(organizationId))

watchEffect(() => {
    if (payload?.value?.status == 200)
        jsonDl.value = payload.value.data.jsonPayload
    if (payload?.value?.status == 404)
        return navigateTo('/organizations')
            .then(_ => toast.error({ title: 'Error al cargar la Organización' }))
})
</script>

<template>
    <div v-if="status != 'success'" class="grid place-content-center h-screen">
        <span class="loading loading-ring loading-xl"></span>
    </div>
    <template v-else>
        <div class="card bg-base-200 shadow-xl">
            <div class="card-body">
                <div class="flex flex-col lg:flex-row gap-5 items-center">
                    <div class="avatar">
                        <div class="h-56 w-56">
                            <img class="drop-shadow p-0.5" :src="payload.data.logoUrl" alt="Logo">
                        </div>
                    </div>
                    <div class="flex flex-col justify-center">
                        <span class="font-bold text-2xl">{{ payload.data.name }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="grid lg:grid-cols-2 gap-5 mt-5">
            <div class="card bg-base-200 shadow-xl">
                <div class="card-body">
                    <h3 class="card-title">Detalles</h3>
                    <div class="divider m-0"></div>
                    <p>{{ payload.data.description }}</p>
                    <div>
                        <NuxtLink :to="payload.data.url" external target="_blank"
                            class="btn btn-link justify-start p-0">
                            <Icon name="material-symbols:language" class="text-xl" />
                            {{ payload.data.url }}
                        </NuxtLink>
                        <br>
                        <button @click="copy(payload.data.email, 'Email copiado')"
                            class="btn btn-link justify-start p-0">
                            <Icon name="material-symbols:mail" class="text-xl" />
                            {{ payload.data.email }}
                        </button>
                    </div>
                </div>
            </div>

            <div class="card bg-base-200 shadow-xl">
                <div class="card-body">
                    <div class="flex justify-between">
                        <h3 class="card-title">Metadata</h3>
                        <div class="flex gap-1">
                            <button class="btn btn-sm" @click="copy(JSON.stringify(jsonDl), 'JSON-DL copiado')">
                                <Icon name="material-symbols:content-copy" class="text-xl" />
                            </button>
                            <NuxtLink external :to="jsonDl.id" target="_blank" class="btn btn-sm">
                                <Icon name="material-symbols:open-in-new" class="text-xl" />
                            </NuxtLink>
                        </div>
                    </div>
                    <div class="divider m-0"></div>
                    <div>
                        <p class="font-bold">@CONTEXT</p>
                        <input type="text" class="input w-full" readonly :value="jsonDl['@context']">
                    </div>
                    <div class="flex gap-3">
                        <div class="flex-1">
                            <p class="font-bold">TYPE</p>
                            <input type="text" class="input w-full" readonly :value="jsonDl.type">
                        </div>
                        <div class="flex-1">
                            <p class="font-bold">ID (CANONICAL)</p>
                            <input type="text" class="input w-full" readonly :value="organizationId">
                        </div>
                    </div>
                    <div>
                        <p class="font-bold">REVOCATION LIST</p>
                        <div class="join w-full">
                            <input type="text" class="input join-item w-full" readonly :value="jsonDl.revocationList">
                            <NuxtLink external :to="jsonDl.revocationList" target="_blank" class="btn join-item">
                                <Icon name="material-symbols:open-in-new" class="text-xl" />
                            </NuxtLink>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <OnlySuperUsersOrMembers :issuer-uuid="organizationId" :roles="[MemberRole.OWNER]">
            <OrganizationsMembers :issuer-uuid="organizationId" />
        </OnlySuperUsersOrMembers>
        <OrganizationsBadges :issuer-uuid="organizationId" />
    </template>
</template>
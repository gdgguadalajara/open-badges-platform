<script setup>
import { getApiV2IssuersIssuerUuidBadges } from '~/services/issuer-badge-class-resource/issuer-badge-class-resource';

const toast = useToast()
const { issuerUuid } = defineProps(['issuerUuid'])

const { params, setParam } = useParams('getApiV2IssuersIssuerUuidBadges' + issuerUuid + 'Params', { page: 1, sort: 'name' })
const { data: paginatedBadges, status, refresh } = useLazyAsyncData('getApiV2IssuersIssuerUuidBadges' + issuerUuid,
    () => getApiV2IssuersIssuerUuidBadges(issuerUuid, params.value),
    { transform: data => data.data })

const prevPage = _ => setParam('page', params.value.page - 1)
const nextPage = _ => setParam('page', params.value.page + 1)
watch(params, _ => refresh())
</script>

<template>
    <div class="card bg-base-200 shadow-xl mt-5">
        <div class="card-body">
            <h3 class="card-title">Credenciales</h3>
            <div class="divider m-0"></div>
            <OnlyMembers :issuer-uuid="issuerUuid">
                <div class="mb-3">
                    <NuxtLink :to="`/organizations/${issuerUuid}/badges/new`" class="btn btn-primary">
                        <Icon name="material-symbols:workspace-premium" class="text-2xl" />
                        Nueva credencial
                    </NuxtLink>
                </div>
            </OnlyMembers>
            <div v-if="status !== 'success'" class="grid place-items-center">
                <span class="loading loading-ring loading-xl"></span>
            </div>
            <div v-else>
                <div v-if="paginatedBadges.data.length === 0" class="text-center text-gray-500">
                    No hay credenciales disponibles.
                </div>
                <div v-else>
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-6 gap-3 mb-4">
                        <NuxtLink v-for="badge in paginatedBadges.data" :key="badge.id" :to="`/badges/${badge.id}`"
                            class="card shadow hover:shadow-xl transition tooltip" :data-tip="badge.name">
                            <div class="card-body p-2">
                                <img :src="badge.jsonPayload.image" :alt="badge.name">
                            </div>
                        </NuxtLink>
                    </div>
                    <div class="grid place-items-center mt-4">
                        <div class="join" v-if="status == 'success'">
                            <button class="join-item btn" :class="{ 'btn-disabled': !paginatedBadges?.meta?.prevPage }"
                                @click="prevPage">«</button>
                            <button class="join-item btn btn-active cursor-auto">Page {{
                                paginatedBadges?.meta?.currentPage }}</button>
                            <button class="join-item btn" :class="{ 'btn-disabled': !paginatedBadges?.meta?.nextPage }"
                                @click="nextPage">»</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
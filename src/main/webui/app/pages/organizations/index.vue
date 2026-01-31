<script setup>
import { getApiV2Issuers } from '~/services/issuer-resource/issuer-resource';

definePageMeta({
    title: 'Organizaciones'
})

const { params, setParam } = useParams('getApiV2Issuers' + 'Params', { page: 1, sort: 'name' })
const { data: paginatedIssuers, status, refresh } = useLazyAsyncData(() => getApiV2Issuers(params.value),
    { transform: (data) => data.data })

const prevPage = _ => setParam('page', params.value.page - 1)
const nextPage = _ => setParam('page', params.value.page + 1)
watch(params, _ => refresh())
</script>

<template>
    <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
            <div class="mb-3">
                <NuxtLink to="/organizations/new" class="btn btn-primary flex-none">
                    <Icon name="material-symbols:domain" class="text-2xl" />
                    Nueva Organización
                </NuxtLink>
            </div>
            <div class="overflow-x-auto rounded-box border border-base-content/5 bg-base-100">
                <table class="table table-zebra">
                    <thead class="bg-base-200">
                        <tr>
                            <th>Organización</th>
                            <th>Descripción</th>
                            <th>Contacto</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="status == 'pending'">
                            <th colspan="4">
                                <div class="grid place-items-center">
                                    <span class="loading loading-ring loading-xl"></span>
                                </div>
                            </th>
                        </tr>
                        <tr v-if="status == 'success'" v-for="issuer in paginatedIssuers?.data" :key="issuer.id">
                            <td>
                                <div class="flex gap-3 items-center">
                                    <div class="avatar">
                                        <div class="h-12 w-12">
                                            <img class="drop-shadow p-0.5" :src="issuer.logoUrl" alt="Logo">
                                        </div>
                                    </div>
                                    <div class="flex flex-col">
                                        <p class="font-bold">{{ issuer.name }}</p>
                                        <NuxtLink :to="issuer.url" target="_blank" external class="underline">
                                            {{ issuer.url }}
                                        </NuxtLink>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="tooltip" :data-tip="issuer.description">
                                    {{ issuer.description.slice(0, 20) }}...
                                </div>
                            </td>
                            <td>
                                <button class="btn btn-link" @click="copy(issuer.email, 'Email copiado')">
                                    {{ issuer.email }}
                                </button>
                            </td>
                            <td>
                                <div class="flex gap-1">
                                    <NuxtLink :to="`/organizations/${issuer.id}`"
                                        class="btn btn-outline btn-sm btn-primary tooltip" data-tip="Configuración">
                                        <Icon name="material-symbols:settings" class="text-xl" />
                                    </NuxtLink>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="grid place-items-center mt-4">
                <div class="join" v-if="status == 'success'">
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedIssuers?.meta?.prevPage }"
                        @click="prevPage">«</button>
                    <button class="join-item btn btn-active cursor-auto">Page {{
                        paginatedIssuers?.meta?.currentPage }}</button>
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedIssuers?.meta?.nextPage }"
                        @click="nextPage">»</button>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { getApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertions } from '~/services/issuer-analytics-resource/issuer-analytics-resource'
import type { AssertionStatus } from '~/services/issuer-analytics-resource/issuer-analytics-resource'

const props = defineProps({
    issuerUuid: { type: String, required: true },
    badgeClassUuid: { type: String, required: true },
})

const { params, setParam, clearParam } = useParams('badgeAssertions' + props.badgeClassUuid + 'Params', { page: 1, size: 10, sort: 'issuedOn,desc' })
const { data: paginated, status, refresh } = useLazyAsyncData(
    () => getApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertions(props.issuerUuid, props.badgeClassUuid, params.value),
    { transform: (data) => data.data },
)

const statusOptions = ['CLAIMED', 'PENDING', 'REVOKED']

const applyStatus = (event) => setParam('status', event.target.value || null)
const applySearch = (event) => setParam('search', event.target.value || null)
const applyFrom = (event) => setParam('from', event.target.value || null)
const applyTo = (event) => setParam('to', event.target.value || null)
const resetFilters = () => { clearParam('status'); clearParam('search'); clearParam('from'); clearParam('to') }

const statusBadge = (s: AssertionStatus) => ({
    CLAIMED: 'badge-success',
    PENDING: 'badge-gold',
    REVOKED: 'badge-danger',
}[s] || 'badge-neutral')

const statusLabel = (s: AssertionStatus) => ({
    CLAIMED: 'Reclamada',
    PENDING: 'Pendiente',
    REVOKED: 'Revocada',
}[s] || s)

const prevPage = () => setParam('page', params.value.page - 1)
const nextPage = () => setParam('page', params.value.page + 1)
watch(params, () => refresh())
</script>

<template>
    <div class="card overflow-hidden">
        <div class="card-pad pb-4 space-y-4">
            <div class="flex flex-wrap items-end justify-between gap-4">
                <div>
                    <h2 class="font-display text-lg font-bold">Recipientes</h2>
                    <p class="text-sm text-ink-soft">Personas que recibieron esta credencial.</p>
                </div>
                <button class="btn btn-ghost btn-sm" @click="resetFilters">
                    <Icon name="material-symbols:filter-alt-off" class="text-lg" />
                    Limpiar filtros
                </button>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                <div class="field">
                    <label class="field-label" for="f-status">Estado</label>
                    <select id="f-status" class="select" :value="params.status || ''" @change="applyStatus">
                        <option value="">Todos</option>
                        <option v-for="s in statusOptions" :key="s" :value="s">{{ s }}</option>
                    </select>
                </div>
                <div class="field">
                    <label class="field-label" for="f-search">Búsqueda</label>
                    <input id="f-search" type="text" class="input" placeholder="Email o nombre..." :value="params.search || ''" @input="applySearch" />
                </div>
                <div class="field">
                    <label class="field-label" for="f-from">Desde</label>
                    <input id="f-from" type="date" class="input" :value="params.from || ''" @change="applyFrom" />
                </div>
                <div class="field">
                    <label class="field-label" for="f-to">Hasta</label>
                    <input id="f-to" type="date" class="input" :value="params.to || ''" @change="applyTo" />
                </div>
            </div>
        </div>

        <div v-if="status !== 'success'" class="px-6 pb-6">
            <div class="space-y-3">
                <div v-for="i in 4" :key="i" class="skeleton h-12 rounded-lg"></div>
            </div>
        </div>

        <div v-else-if="!paginated?.data?.length" class="empty">
            <span class="flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-soft text-teal">
                <Icon name="material-symbols:group" class="text-2xl" />
            </span>
            <p class="text-sm">No hay recipientes que coincidan con los filtros.</p>
        </div>

        <template v-else>
            <div class="overflow-x-auto">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Recipiente</th>
                            <th>Estado</th>
                            <th class="hidden md:table-cell">Fecha de emisión</th>
                            <th class="text-right">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in paginated.data" :key="item.assertionId">
                            <td>
                                <div class="flex flex-col min-w-0">
                                    <span class="font-medium">{{ item.recipient?.fullName || 'Sin reclamar' }}</span>
                                    <span v-if="item.recipient?.email" class="text-xs text-ink-soft">{{ item.recipient.email }}</span>
                                    <span v-else class="text-xs text-ink-soft italic">Email no revelado (pendiente)</span>
                                </div>
                            </td>
                            <td>
                                <span class="badge" :class="statusBadge(item.status)">
                                    <Icon v-if="item.status === 'CLAIMED'" name="material-symbols:verified" class="text-sm" />
                                    <Icon v-else-if="item.status === 'REVOKED'" name="material-symbols:block" class="text-sm" />
                                    {{ statusLabel(item.status) }}
                                </span>
                            </td>
                            <td class="hidden md:table-cell text-ink-soft tabular">{{ item.issuedOn?.slice(0, 10) }}</td>
                            <td class="text-right">
                                <div class="flex justify-end gap-1">
                                    <a :href="`/api/v2/assertions/${item.assertionId}`" target="_blank" rel="noopener" class="btn btn-outline btn-sm" aria-label="Ver assertion pública">
                                        <Icon name="material-symbols:open-in-new" class="text-lg" />
                                    </a>
                                    <BadgesRevokeAssertionDialog :issuer-uuid="issuerUuid" :badge-class-uuid="badgeClassUuid" :assertion-uuid="item.assertionId"
                                        :is-revoked="item.status === 'REVOKED'" @updated="refresh" />
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="border-t border-line p-4">
                <AppPagination :meta="paginated?.meta" @prev="prevPage" @next="nextPage" />
            </div>
        </template>
    </div>
</template>
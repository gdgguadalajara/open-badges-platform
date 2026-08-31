<script setup>
import { patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidRevoke } from '~/services/issuer-revocation-resource/issuer-revocation-resource'

const props = defineProps({
    issuerUuid: { type: String, required: true },
    badgeClassUuid: { type: String, required: true },
})
const emit = defineEmits(['updated'])

const toast = useToast()
const open = ref(false)
const reason = ref('')
const revokeAll = ref(true)
const selectedIds = ref([])
const loading = ref(false)

const openDialog = () => { reason.value = ''; revokeAll.value = true; selectedIds.value = []; open.value = true }

const submit = () => {
    if (!reason.value.trim()) { toast.error({ title: 'El motivo es obligatorio' }); return }
    if (!revokeAll.value && !selectedIds.value.length) { toast.error({ title: 'Selecciona al menos una emisión' }); return }
    loading.value = true
    patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidRevoke(props.issuerUuid, props.badgeClassUuid, {
        reason: reason.value,
        assertionIds: revokeAll.value ? null : selectedIds.value,
    })
        .then(({ status, data }) => status == 200
            ? (toast.success({ title: `Credenciales revocadas (${data.revoked ?? 0})` }), Promise.resolve())
            : Promise.reject())
        .then(_ => { open.value = false; emit('updated') })
        .catch(_ => toast.error({ title: 'Error al revocar las credenciales' }))
        .finally(() => loading.value = false)
}
</script>

<template>
    <button class="btn btn-danger" @click="openDialog">
        <Icon name="material-symbols:block" class="text-lg" />
        Revocar credencial
    </button>

    <Teleport to="body">
        <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60" @click.self="open = false">
            <div class="w-full max-w-lg rounded-3xl border border-line bg-surface p-6 shadow-card-hover" role="dialog" aria-modal="true">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="font-display text-lg font-bold">Revocar credencial</h3>
                    <button class="btn btn-ghost btn-icon" aria-label="Cerrar" @click="open = false">
                        <Icon name="material-symbols:close" class="text-xl" />
                    </button>
                </div>
                <p class="text-sm text-ink-soft mb-5">
                    Las emisiones seleccionadas se marcarán como revocadas y su estado será visible públicamente.
                    Esta acción puede revertirse individualmente.
                </p>
                <form @submit.prevent="submit" class="flex flex-col gap-5">
                    <div class="field">
                        <label class="field-label" for="bulk-revoke-reason">Motivo *</label>
                        <textarea id="bulk-revoke-reason" v-model="reason" class="textarea" rows="3" placeholder="Describe el motivo de la revocación" required></textarea>
                    </div>

                    <div class="rounded-xl border border-line bg-surface-2 p-4 flex flex-col gap-3">
                        <label class="flex items-center gap-3 cursor-pointer">
                            <input type="radio" v-model="revokeAll" :value="true" class="accent-[var(--danger)]" />
                            <span class="text-sm">Todas las emisiones no revocadas</span>
                        </label>
                        <label class="flex items-center gap-3 cursor-pointer">
                            <input type="radio" v-model="revokeAll" :value="false" class="accent-[var(--danger)]" />
                            <span class="text-sm">Solo emisiones seleccionadas</span>
                        </label>
                    </div>

                    <div v-if="!revokeAll" class="field">
                        <label class="field-label" for="bulk-revoke-ids">IDs de emisión (separados por coma)</label>
                        <input id="bulk-revoke-ids" type="text" class="input font-mono text-xs"
                            placeholder="uuid-1, uuid-2"
                            @input="selectedIds = $event.target.value.split(',').map(s => s.trim()).filter(Boolean)" />
                    </div>

                    <div class="flex justify-end gap-3">
                        <button type="button" class="btn btn-ghost" @click="open = false">Cancelar</button>
                        <button class="btn btn-danger" :disabled="loading">
                            <span v-if="loading" class="spinner"></span>
                            <template v-else>
                                <Icon name="material-symbols:block" class="text-lg" />
                                Revocar
                            </template>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </Teleport>
</template>
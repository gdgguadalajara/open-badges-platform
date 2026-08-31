<script setup lang="ts">
import {
    patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidRevoke,
    patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidUnrevoke,
} from '~/services/issuer-revocation-resource/issuer-revocation-resource'

const props = defineProps({
    issuerUuid: { type: String, required: true },
    badgeClassUuid: { type: String, required: true },
    assertionUuid: { type: String, required: true },
    isRevoked: { type: Boolean, default: false },
})
const emit = defineEmits(['updated'])

const toast = useToast()
const open = ref(false)
const reason = ref('')
const loading = ref(false)

const openDialog = () => { reason.value = ''; open.value = true }

const submitRevoke = () => {
    if (!reason.value.trim()) { toast.error({ title: 'El motivo es obligatorio' }); return }
    loading.value = true
    patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidRevoke(
        props.issuerUuid, props.badgeClassUuid, props.assertionUuid, { reason: reason.value })
        .then(({ status }) => status == 200 ? Promise.resolve() : Promise.reject())
        .then(_ => toast.success({ title: 'Credencial revocada' }))
        .then(_ => { open.value = false; emit('updated') })
        .catch(_ => toast.error({ title: 'Error al revocar la credencial' }))
        .finally(() => loading.value = false)
}

const submitUnrevoke = () => {
    loading.value = true
    patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidUnrevoke(
        props.issuerUuid, props.badgeClassUuid, props.assertionUuid)
        .then(({ status }) => status == 200 ? Promise.resolve() : Promise.reject())
        .then(_ => toast.success({ title: 'Credencial restaurada' }))
        .then(_ => emit('updated'))
        .catch(_ => toast.error({ title: 'Error al restaurar la credencial' }))
        .finally(() => loading.value = false)
}
</script>

<template>
    <button v-if="!isRevoked" class="btn btn-outline btn-sm text-danger" :aria-label="'Revocar credencial'" @click="openDialog">
        <Icon name="material-symbols:block" class="text-lg" />
        Revocar
    </button>
    <button v-else class="btn btn-outline btn-sm" :aria-label="'Restaurar credencial'" @click="submitUnrevoke" :disabled="loading">
        <span v-if="loading" class="spinner"></span>
        <template v-else>
            <Icon name="material-symbols:refresh" class="text-lg" />
            Restaurar
        </template>
    </button>

    <Teleport to="body">
        <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60" @click.self="open = false">
            <div class="w-full max-w-md rounded-3xl border border-line bg-surface p-6 shadow-card-hover" role="dialog" aria-modal="true">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="font-display text-lg font-bold">Revocar credencial</h3>
                    <button class="btn btn-ghost btn-icon" aria-label="Cerrar" @click="open = false">
                        <Icon name="material-symbols:close" class="text-xl" />
                    </button>
                </div>
                <p class="text-sm text-ink-soft mb-4">
                    Esta acción marcará la credencial como revocada y el estado será visible públicamente.
                </p>
                <form @submit.prevent="submitRevoke" class="flex flex-col gap-5">
                    <div class="field">
                        <label class="field-label" for="revoke-reason">Motivo *</label>
                        <textarea id="revoke-reason" v-model="reason" class="textarea" rows="3" placeholder="Describe el motivo de la revocación" required></textarea>
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
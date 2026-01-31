<script setup>
import { postApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertions } from '~/services/issuer-assertion-resource/issuer-assertion-resource';

const { issuerId, badgeId } = defineProps(['issuerId', 'badgeId'])

const toast = useToast()
const isLoading = ref(false)

const emitBadgesSubmit = (e) => {
    if (isLoading.value) return
    isLoading.value = true
    postApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertions(issuerId, badgeId, {
        emails: e.target.emails.value.split(',').map(email => email.trim()),
        evidenceUrl: e.target.evidence.value,
    })
        .then(data => data.status == 200 ? Promise.resolve() : Promise.reject())
        .then(_ => e.target.reset())
        .then(_ => toast.success({ title: 'Insignias emitidas correctamente' }))
        .then(_ => closeModal('emitBadge'))
        .catch(_ => toast.error({ title: 'Error al emitir las insignias' }))
        .finally(() => isLoading.value = false)
}


</script>

<template>
    <div class="flex flex-col w-full lg:w-auto lg:absolute top-5 right-5">
        <button @click="openModal('emitBadge')" class="btn btn-primary">
            Emitir Insignia
        </button>
        <Teleport to="#teleports">
            <dialog id="emitBadge" class="modal">
                <div class="modal-box">
                    <h3 class="text-lg font-bold">Emitir Insignias</h3>
                    <form @submit.prevent="emitBadgesSubmit">
                        <fieldset class="fieldset">
                            <legend class="fieldset-legend">Emails</legend>
                            <textarea class="textarea w-full" name="emails" rows="3" placeholder=""></textarea>
                            <p class="label">Ingrese los emails separados por comas.</p>
                        </fieldset>
                        <fieldset class=" fieldset">
                            <legend class="fieldset-legend">Evidencia</legend>
                            <input type="text" class="input w-full" name="evidence" />
                        </fieldset>
                        <div class="mt-5">
                            <button class="w-full btn btn-primary">
                                <div v-if="isLoading" class="flex items-center gap-3">
                                    <div class="grid place-items-center">
                                        <span class="loading loading-ring loading-xl"></span>
                                    </div>
                                    Enviando...
                                </div>
                                <p v-else>Emitir Insignias</p>
                            </button>
                        </div>
                    </form>
                </div>
                <form method="dialog" class="modal-backdrop">
                    <button>close</button>
                </form>
            </dialog>
        </Teleport>
    </div>
</template>
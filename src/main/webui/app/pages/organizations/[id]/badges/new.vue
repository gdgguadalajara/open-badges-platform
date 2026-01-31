<script setup>
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { postApiV2IssuersIssuerUuidBadges } from '~/services/issuer-badge-class-resource/issuer-badge-class-resource'

const toast = useToast()
const route = useRoute()
const organizationId = route.params.id

const criteria = ref('')
const imageb64 = ref('')

const sanitizedHtml = computed(() => {
    const rawHtml = marked.parse(criteria.value || '')
    return DOMPurify.sanitize(rawHtml)
})

const handleImageUpdate = (base64) => imageb64.value = base64

const onSubmitCreateBadge = (e) =>
    postApiV2IssuersIssuerUuidBadges(organizationId, {
        name: e.target.badge.value,
        description: e.target.description.value,
        criteriaMd: criteria.value,
        imageBase64: imageb64.value
    })
        .then(data => data.status != 200 ? Promise.reject() : Promise.resolve())
        .then(_ => e.target.reset())
        .then(_ => toast.success({ title: 'Credencial creada' }))
        .then(_ => navigateTo(`/organizations/${organizationId}`))
        .catch(_ => toast.error({ title: 'Error al crear la Credencial' }))
</script>

<template>
    <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
            <form @submit.prevent="onSubmitCreateBadge" class="grid gap-2">
                <OrganizationsBadgeImageUploader @update:image="handleImageUpdate" />
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Credencial</legend>
                    <input type="text" class="input w-full" name="badge" />
                </fieldset>
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Descripción</legend>
                    <textarea class="textarea w-full" name="description" rows="5"></textarea>
                </fieldset>
                <legend class="fieldset-legend text-xs">Criterios</legend>
                <div class="tabs tabs-lift">
                    <input type="radio" name="criteria_tabs" class="tab" aria-label="Editar" checked />
                    <div class="tab-content bg-base-100 border-base-300 p-6">
                        <fieldset class="fieldset">
                            <textarea v-model="criteria" class="textarea w-full" name="criteria" rows="15"></textarea>
                        </fieldset>
                    </div>
                    <input type="radio" name="criteria_tabs" class="tab" aria-label="Previsualizar" />
                    <div class="tab-content bg-base-100 border-base-300 p-6">
                        <div class="prose max-w-none" v-html="sanitizedHtml"></div>
                    </div>
                </div>
                <div class="w-full lg:grid lg:justify-end mt-3">
                    <button class="w-full btn btn-primary lg:w-60">Crear nueva credencial</button>
                </div>
            </form>
        </div>
    </div>
</template>
<script setup>
import { postApiV2Issuers } from '~/services/issuer-resource/issuer-resource';

definePageMeta({
    title: 'Nueva Organización'
})
    
const toast = useToast()

const onSubmitCreateOrganization = (e) =>
    postApiV2Issuers({
        name: e.target.name.value,
        url: e.target.url.value,
        logoUrl: e.target.logoUrl.value,
        email: e.target.email.value,
        description: e.target.description.value,
    })
        .then(_ => e.target.reset())
        .then(_ => toast.success({ title: 'Organización creada' }))
        .then(_ => navigateTo("/organizations"))
        .catch(_ => toast.error({ title: 'Error al crear la Organización' }))
</script>

<template>
    <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
            <form @submit.prevent="onSubmitCreateOrganization" class="grid grid-cols-1 lg:grid-cols-2 gap-2">
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Organización</legend>
                    <input type="text" class="input w-full" name="name" />
                </fieldset>
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Web</legend>
                    <input type="url" class="input w-full" name="url" />
                </fieldset>
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Logo URL</legend>
                    <input type="url" class="input w-full" name="logoUrl" />
                </fieldset>
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Email de contacto</legend>
                    <input type="email" class="input w-full" name="email" />
                </fieldset>
                <fieldset class="fieldset lg:col-span-2">
                    <legend class="fieldset-legend">Descripción</legend>
                    <textarea class="textarea w-full" name="description"></textarea>
                </fieldset>
                <div class="lg:col-span-2 lg:grid lg:justify-end mt-3">
                    <button class="w-full btn btn-primary lg:w-60">Crear nueva organización</button>
                </div>
            </form>
        </div>
    </div>
</template>
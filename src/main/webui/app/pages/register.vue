<script setup>
import { postApiAuthRegister } from '~/services/authentication-resource/authentication-resource';

const toast = useToast()


const onCreateAccountSubmit = (e) => postApiAuthRegister({
    name: e.target.name.value,
    acceptedLegal: e.target.legal.checked
})
    .then(({ data, status }) => status == 200 ? Promise.resolve(data) : Promise.reject(data))
    .then(() => navigateTo('/', { external: true }))
    .catch((data) => toast.error({ message: data.message }))

</script>

<template>
    <div class="grid place-items-center">
        <div class="card bg-base-200 shadow max-w-2xl w-full">
            <div class="card-body">
                <h2 class="card-title text-2xl">Registro</h2>
                <div class="divider m-0"></div>
                <form @submit.prevent="onCreateAccountSubmit">
                    <fieldset class="fieldset">
                        <legend class="fieldset-legend">Nombre completo</legend>
                        <input type="text" class="input w-full" name="name" />
                    </fieldset>
                    <fieldset class="fieldset ">
                        <label class="label">
                            <input type="checkbox" name="legal" class="checkbox" />
                            He leído la
                            <NuxtLink to="/privacy" target="_blank" class="btn btn-link p-0">
                                politica de privacidad
                            </NuxtLink>
                            y acepto los
                            <NuxtLink to="/terms" target="_blank" class="btn btn-link p-0">
                                términos y condiciones
                            </NuxtLink>
                        </label>
                    </fieldset>
                    <button class="btn btn-primary btn-block mt-5">
                        Crear cuenta
                    </button>
                </form>
            </div>
        </div>
    </div>
</template>
<script setup>
import { getApiMeAccountConfirmLinkedEmailsCode, postApiMeAccountLinkedEmails, putApiMeAccount } from '~/services/my-account-resource/my-account-resource';

const { me, fetchMe } = useAuth()
const toast = useToast()
const router = useRouter()
const route = useRoute()

const onEditNameSubmit = (e) => putApiMeAccount({
    name: e.target.name.value
})
    .then(_ => fetchMe())
    .then(_ => e.target.reset())
    .then(_ => closeModal('edit-name'))
    .then(_ => toast.success({ message: 'Nombre actualizado' }))
    .catch(_ => toast.error({ message: 'Error al actualizar el nombre' }))

const onLinkEmailSubmit = (e) => postApiMeAccountLinkedEmails({
    email: e.target.email.value
})
    .then(_ => e.target.reset())
    .then(_ => closeModal('link-new-email'))
    .then(_ => toast.success({ message: 'Vinculación de email en proceso, revisa tu bandeja de entrada' }))
    .catch(_ => toast.error({ message: 'Error al vincular el email' }))

const onConfirmLinkEmailSubmit = (e) => confirmEmail(e.target.code.value)

const confirmEmail = (code) => getApiMeAccountConfirmLinkedEmailsCode(code)
    .then(({ status }) => status == 200 ? Promise.resolve() : Promise.reject())
    .then(_ => toast.success({ message: 'Correo verificado con éxito! Tus insignias han sido reclamadas.' }))
    .then(_ => fetchMe())
    .catch(_ => toast.error({ message: 'Hubo un problema al verificar tu correo. El código podría haber expirado.' }))
    .finally(_ => e.target.reset())
    .finally(_ => router.replace({ query: {} }))
    .finally(_ => closeModal('confirm-email'))

onMounted(() => {
    if (route.query.verify)
        confirmEmail(route.query.verify)
})
</script>

<template>
    <div class="card bg-base-200 shadow">
        <div class="card-body">
            <div class="flex justify-between items-center">
                <div class="flex items-center gap-2">
                    <div class="tooltip tooltip-right"
                        data-tip="Asegúrate de que tu nombre esté escrito correctamente en tu perfil. Así evitaremos errores en la emisión de tus certificados. Recuerda que saldrá exactamente como lo hayas registrado.">
                        <Icon name="material-symbols:info" class=" text-primary text-2xl" />
                    </div>
                    <h2 class="card-title font-bold text-2xl">
                        {{ me?.account?.fullName }}
                    </h2>
                </div>
                <button @click="openModal('edit-name')" class="btn btn-primary">
                    <Icon name="material-symbols:edit" class="text-2xl" />
                </button>
            </div>

            <div class="divider m-0"></div>

            <div class="flex flex-col md:flex-row justify-between items-center">
                <h3 class="font-bold text-lg">Emails:</h3>
                <div class="flex flex-col md:flex-row gap-2 w-full md:w-auto">
                    <button @click="openModal('link-new-email')" class="btn btn-primary btn-block md:w-auto">
                        <Icon name="material-symbols:mail" class="text-2xl" />
                        Vincular nuevo email
                    </button>

                    <button @click="openModal('confirm-email')" class="btn btn-secondary btn-block md:w-auto">
                        <Icon name="material-symbols:mark-email-read" class="text-2xl" />
                        Confirmar vinculación
                    </button>
                </div>
            </div>
            <p>{{ me?.account?.email }} (Predeterminado)</p>
            <p v-for="email in me?.linkedEmails">
                {{ email }}
            </p>
        </div>
    </div>

    <dialog id="edit-name" class="modal">
        <div class="modal-box">
            <h3 class="text-lg font-bold">Actualizar nombre</h3>
            <div class="divider m-0"></div>
            <form @submit.prevent="onEditNameSubmit">
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Nombre</legend>
                    <input type="text" class="input w-full" name="name" />
                </fieldset>
                <button class="btn btn-primary btn-block mt-5">
                    Actualizar
                </button>
            </form>
        </div>
        <form method="dialog" class="modal-backdrop">
            <button>close</button>
        </form>
    </dialog>

    <dialog id="confirm-email" class="modal">
        <div class="modal-box">
            <h3 class="text-lg font-bold">Codigo de confirmación</h3>
            <div class="divider m-0"></div>
            <form @submit.prevent="onConfirmLinkEmailSubmit">
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Codigo</legend>
                    <input type="text" class="input w-full" name="code" />
                </fieldset>
                <button class="btn btn-primary btn-block mt-5">
                    Confirmar
                </button>
            </form>
        </div>
        <form method="dialog" class="modal-backdrop">
            <button>close</button>
        </form>
    </dialog>

    <dialog id="link-new-email" class="modal">
        <div class="modal-box">
            <h3 class="text-lg font-bold">Vincular nuevo email</h3>
            <div class="divider m-0"></div>
            <form @submit.prevent="onLinkEmailSubmit">
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Email</legend>
                    <input type="email" class="input w-full" name="email" />
                </fieldset>
                <button class="btn btn-primary btn-block mt-5">
                    Vincular
                </button>
            </form>
        </div>
        <form method="dialog" class="modal-backdrop">
            <button>close</button>
        </form>
    </dialog>
</template>
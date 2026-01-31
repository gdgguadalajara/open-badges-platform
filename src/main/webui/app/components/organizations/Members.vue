<script setup>
import { MemberRole } from '~/models/memberRole'
import { getApiV2IssuersIssuerUuidMembers } from '~/services/issuer-resource/issuer-resource';
import { deleteApiMembershipsIssuersIssuerUuidAccountsAccountUuid, postApiMembershipsIssuersIssuerUuidAccounts } from '~/services/membership-resource/membership-resource';

const toast = useToast()
const { issuerUuid } = defineProps(['issuerUuid'])
const { me, fetchMe } = useAuth()

const { params, setParam } = useParams('getApiV2IssuersIssuerUuidMembers' + 'Params', { page: 1, sort: 'name' })
const { data: paginatedMembers, status, refresh } = useLazyAsyncData(() => getApiV2IssuersIssuerUuidMembers(issuerUuid, params.value),
    { transform: data => data.data })

const prevPage = _ => setParam('page', params.value.page - 1)
const nextPage = _ => setParam('page', params.value.page + 1)
watch(params, _ => refresh())

const createNewMember = (e) => postApiMembershipsIssuersIssuerUuidAccounts(issuerUuid, {
    email: e.target.email.value,
    role: e.target.role.value,
})
    .then(({ data, status }) => status != 200 ? Promise.reject(data) : Promise.resolve(data))
    .then(data => me.value.account.email == data.account.email ? fetchMe() : Promise.resolve())
    .then(_ => e.target.reset())
    .then(_ => refresh())
    .then(_ => toast.success({ title: 'Miembro creado' }))
    .then(_ => closeModal('create-member-modal'))
    .catch(data => toast.error({ title: data.message }))

const deleteMember = (member) => deleteApiMembershipsIssuersIssuerUuidAccountsAccountUuid(member.issuer.id, member.account.id)
    .then(_ => me.value.account.email == member.account.email ? fetchMe() : Promise.resolve())
    .then(_ => refresh())
</script>

<template>
    <div class="card bg-base-200 shadow-xl mt-5">
        <div class="card-body">
            <h3 class="card-title">Miembros</h3>
            <div class="divider m-0"></div>
            <div class="mb-3">
                <button class="btn btn-primary" @click="openModal('create-member-modal')">
                    <Icon name="material-symbols:person-add" class="text-2xl" />
                    Nuevo miembro
                </button>
            </div>
            <div class="overflow-x-auto rounded-box border border-base-content/5 bg-base-100">
                <table class="table table-zebra">
                    <thead class="bg-base-200">
                        <tr>
                            <th>#</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Role</th>
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
                        <tr v-if="status == 'success'" v-for="membership in paginatedMembers?.data"
                            :key="membership.id">
                            <td>
                                <button class="btn btn-link" @click="copy(membership.id, 'ID copiado')">
                                    {{ membership.id.slice(0, 8) }}...
                                </button>
                            </td>
                            <td>{{ membership.account.fullName }}</td>
                            <td>
                                <button class="btn btn-link" @click="copy(membership.account.email, 'Email copiado')">
                                    {{ membership.account.email }}
                                </button>
                            </td>
                            <td>{{ membership.role }}</td>
                            <td>
                                <div class="flex gap-1">
                                    <button @click="openModal(`delete-member-modal-${membership.id}`)"
                                        class="btn btn-outline btn-sm btn-error tooltip" data-tip="Eliminar">
                                        <Icon name="material-symbols:delete" class="text-xl" />
                                    </button>
                                    <dialog :id="`delete-member-modal-${membership.id}`" class="modal">
                                        <div class="modal-box">
                                            <h3 class="text-lg font-bold">Eliminar miembro</h3>
                                            <div class="divider m-0"></div>
                                            <p>
                                                ¿Confirma la eliminación del miembro {{ membership.account.fullName }}?
                                            </p>
                                            <div class="mt-5">
                                                <button @click="deleteMember(membership)"
                                                    class="w-full btn btn-error">Eliminar miembro</button>
                                            </div>
                                        </div>
                                        <form method="dialog" class="modal-backdrop">
                                            <button>close</button>
                                        </form>
                                    </dialog>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="grid place-items-center mt-4">
                <div class="join" v-if="status == 'success'">
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedMembers?.meta?.prevPage }"
                        @click="prevPage">«</button>
                    <button class="join-item btn btn-active cursor-auto">Page {{
                        paginatedMembers?.meta?.currentPage }}</button>
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedMembers?.meta?.nextPage }"
                        @click="nextPage">»</button>
                </div>
            </div>
        </div>
    </div>
    <dialog id="create-member-modal" class="modal">
        <div class="modal-box">
            <h3 class="text-lg font-bold">Nuevo miembro</h3>
            <form @submit.prevent="createNewMember">
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Email</legend>
                    <input type="email" class="input w-full" name="email" />
                </fieldset>
                <fieldset class="fieldset">
                    <legend class="fieldset-legend">Rol</legend>
                    <select class="select w-full" name="role">
                        <option disabled selected>Selecciona un rol</option>
                        <option :value="value" v-for="value in MemberRole">
                            {{ value }}
                        </option>
                    </select>
                </fieldset>
                <div class="mt-5">
                    <button class="w-full btn btn-primary">Crear miembro</button>
                </div>
            </form>
        </div>
        <form method="dialog" class="modal-backdrop">
            <button>close</button>
        </form>
    </dialog>
</template>
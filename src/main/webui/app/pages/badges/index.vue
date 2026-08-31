<script setup>
import { getApiAdminMeAssertions } from '~/services/admin-resource/admin-resource';
import { putApiMeAssertionsUuidVisibility } from '~/services/my-assertion-resource/my-assertion-resource';

const { me } = useAuth()
const toast = useToast()
const { params, setParam } = useParams('getApiAdminMeAssertions' + 'Params', { page: 1, sort: 'name' })
const { data: paginatedAssertions, status, refresh } = useLazyAsyncData(() => getApiAdminMeAssertions(params.value),
    { transform: (data) => data.data })

const profileUrl = computed(() => `${window.location.origin}/u/${me.value?.account?.id}`)

const copyProfileUrl = () => copy(profileUrl.value, 'Enlace de perfil copiado')

const togglingId = ref(null)

const toggleVisibility = (assertion) => {
    if (assertion.isRevoked)
        return toast.error({ message: 'No puedes cambiar la visibilidad de una credencial revocada' })
    togglingId.value = assertion.id
    putApiMeAssertionsUuidVisibility(assertion.id, { isPublic: !assertion.isPublic })
        .then(({ status }) => status == 200 ? Promise.resolve() : Promise.reject())
        .then(_ => {
            assertion.isPublic = !assertion.isPublic
            toast.success({ message: `"${assertion.badgeClass.name}" ${assertion.isPublic ? 'ahora es pública' : 'ahora es privada'}` })
        })
        .catch(_ => toast.error({ message: 'Error al cambiar la visibilidad' }))
        .finally(_ => { togglingId.value = null })
}

const open = (assertion) => {
    const features = { toolbar: false, location: false, menubar: false, width: 800, height: 600, scrollbars: true }
    window.open(`/api/v2/assertions/${assertion.id}`, assertion.badgeClass.name,
        Object.keys(features).map(key => `${key}=${features[key]}`).join(','))
}

const assertionPublicUrl = (assertion) => `${window.location.origin}/api/v2/assertions/${assertion.id}`

const generateShareOnLinkedinUrl = (assertion) => {
    const date = new Date(assertion.issuedOn)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const params = new URLSearchParams({
        startTask: 'CERTIFICATION_NAME', name: assertion.badgeClass.name,
        organizationName: assertion.badgeClass.issuer.name, issueYear: year.toString(),
        issueMonth: month.toString(), certId: assertion.id, certUrl: assertionPublicUrl(assertion),
    })
    return `https://www.linkedin.com/profile/add?${params.toString()}`
}

const generateShareOnTwitterUrl = (assertion) => {
    const text = encodeURIComponent(`¡Acabo de obtener la insignia "${assertion.badgeClass.name}" en ${assertion.badgeClass.issuer.name}!`)
    const url = encodeURIComponent(assertionPublicUrl(assertion))
    const hashtags = "Java,Quarkus,OpenBadges"
    return `https://twitter.com/intent/tweet?text=${text}&url=${url}&hashtags=${hashtags}`
}

const shareDialog = ref(null)

const openShare = (assertion) => {
    shareDialog.value = assertion
    document.getElementById('share-dialog')?.showModal()
}

const prevPage = _ => setParam('page', params.value.page - 1)
const nextPage = _ => setParam('page', params.value.page + 1)
watch(params, _ => refresh())
</script>

<template>
    <div>
        <div class="page-head flex-row items-end justify-between">
            <div>
                <h1 class="page-title">Mis credenciales</h1>
                <p class="page-sub">Tus insignias digitales emitidas y verificables.</p>
            </div>
            <div class="flex items-center gap-2 flex-wrap">
                <span v-if="paginatedAssertions?.meta" class="badge badge-teal tabular">
                    {{ paginatedAssertions.meta.totalRecords ?? 0 }} credenciales
                </span>
                <button v-if="me" class="btn btn-outline btn-sm" @click="copyProfileUrl">
                    <Icon name="material-symbols:share" class="text-lg" />
                    Compartir mi perfil
                </button>
            </div>
        </div>

        <!-- Loading skeletons -->
        <div v-if="status !== 'success' && !paginatedAssertions" class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-5">
            <div v-for="i in 10" :key="i" class="card p-4">
                <div class="skeleton aspect-square rounded-2xl"></div>
                <div class="skeleton h-4 w-3/4 mt-4"></div>
                <div class="skeleton h-3 w-1/2 mt-2"></div>
            </div>
        </div>

        <!-- Empty -->
        <div v-else-if="!paginatedAssertions?.data?.length" class="card">
            <div class="empty">
                <span class="flex h-16 w-16 items-center justify-center rounded-2xl bg-teal-soft text-teal">
                    <Icon name="material-symbols:workspace-premium" class="text-3xl" />
                </span>
                <h3 class="font-display text-lg font-bold text-ink">Aún no tienes credenciales</h3>
                <p class="max-w-sm text-sm">Cuando una organización te emita una insignia, aparecerá aquí para que puedas verla, compartirla y descargarla.</p>
            </div>
        </div>

        <!-- Grid -->
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-5">
            <div v-for="assertion in paginatedAssertions.data" :key="assertion.id" class="card card-hover p-4 group">
                <button @click="open(assertion)" class="block w-full" :aria-label="`Ver ${assertion.badgeClass.name}`">
                    <div class="relative">
                        <img :src="assertion.badgeClass.jsonPayload.image" :alt="assertion.badgeClass.name"
                            class="aspect-square w-full rounded-2xl object-cover transition-transform duration-500 group-hover:scale-[1.02]"
                            :class="assertion.isRevoked ? 'opacity-60 grayscale' : ''" />
                        <span v-if="assertion.isRevoked"
                            class="absolute inset-0 flex items-center justify-center">
                            <span class="inline-flex items-center gap-1.5 rounded-full bg-danger px-3 py-1.5 text-xs font-bold text-white shadow-card-hover">
                                <Icon name="material-symbols:block" class="text-base" />
                                Revocada
                            </span>
                        </span>
                    </div>
                </button>
                <div class="mt-4 flex flex-col gap-1">
                    <h3 class="font-display text-sm font-bold leading-snug line-clamp-2">{{ assertion.badgeClass.name }}</h3>
                    <p class="text-xs text-ink-soft line-clamp-1">{{ assertion.badgeClass.issuer?.name }}</p>
                    <div class="flex items-center gap-1.5 mt-1">
                        <span v-if="assertion.isRevoked" class="badge badge-danger">
                            <Icon name="material-symbols:block" class="text-sm" />
                            Revocada
                        </span>
                        <span v-else class="badge badge-success">
                            <Icon name="material-symbols:verified" class="text-sm" />
                            Verificada
                        </span>
                        <span :class="assertion.isPublic ? 'badge badge-teal' : 'badge badge-neutral'">
                            <Icon :name="assertion.isPublic ? 'material-symbols:public' : 'material-symbols:lock'" class="text-sm" />
                            {{ assertion.isPublic ? 'Pública' : 'Privada' }}
                        </span>
                    </div>
                    <p v-if="assertion.isRevoked && assertion.revocationReason" class="text-xs text-danger mt-1 line-clamp-2">
                        {{ assertion.revocationReason }}
                    </p>
                </div>
                <div class="mt-4 flex gap-2">
                    <button @click="toggleVisibility(assertion)" class="btn btn-outline btn-sm flex-1"
                        :disabled="togglingId == assertion.id || assertion.isRevoked"
                        :aria-label="assertion.isPublic ? 'Hacer privada' : 'Hacer pública'">
                        <Icon v-if="togglingId == assertion.id" name="material-symbols:progress-activity" class="text-lg animate-spin" />
                        <Icon v-else :name="assertion.isPublic ? 'material-symbols:visibility-off' : 'material-symbols:visibility'" class="text-lg" />
                    </button>
                    <button v-if="!assertion.isRevoked" @click="openShare(assertion)" class="btn btn-outline btn-sm flex-1" aria-label="Compartir">
                        <Icon name="material-symbols:share" class="text-lg" />
                    </button>
                    <a :href="`/api/admin/assertions/${assertion.id}/bakedimage`" target="_blank" rel="noopener"
                        class="btn btn-navy btn-sm flex-1" aria-label="Descargar insignia">
                        <Icon name="material-symbols:download" class="text-lg" />
                    </a>
                </div>
            </div>
        </div>

        <div class="mt-8">
            <AppPagination :meta="paginatedAssertions?.meta" @prev="prevPage" @next="nextPage" />
        </div>
    </div>

    <!-- Share dialog -->
    <dialog id="share-dialog" class="fixed inset-0 z-50 m-auto w-full max-w-md rounded-3xl border border-line bg-surface p-6 shadow-card-hover backdrop:bg-black/50">
        <div class="flex items-center justify-between mb-4">
            <h3 class="font-display text-lg font-bold">{{ shareDialog?.badgeClass?.name }}</h3>
            <button class="btn btn-ghost btn-icon" aria-label="Cerrar" @click="document.getElementById('share-dialog')?.close()">
                <Icon name="material-symbols:close" class="text-xl" />
            </button>
        </div>
        <p class="text-sm text-ink-soft mb-5">Comparte tu credencial o copia su enlace público de verificación.</p>
        <div class="grid gap-3">
            <a v-if="shareDialog" :href="generateShareOnLinkedinUrl(shareDialog)" target="_blank" rel="noopener" class="btn btn-navy btn-block">
                <Icon name="material-symbols:linkedin" class="text-lg" />
                Compartir en LinkedIn
            </a>
            <a v-if="shareDialog" :href="generateShareOnTwitterUrl(shareDialog)" target="_blank" rel="noopener" class="btn btn-outline btn-block">
                <Icon name="material-symbols:alternate-email" class="text-lg" />
                Compartir en X / Twitter
            </a>
            <button v-if="shareDialog" @click="copy(assertionPublicUrl(shareDialog), 'Enlace público copiado')" class="btn btn-primary btn-block">
                <Icon name="material-symbols:link" class="text-lg" />
                Copiar enlace público
            </button>
        </div>
    </dialog>
</template>

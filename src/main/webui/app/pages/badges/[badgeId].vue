<script setup>
import { marked } from 'marked';
import DOMPurify from 'dompurify';
import { MemberRole } from '~/models'
import { getApiAdminBadgesUuid } from '~/services/admin-resource/admin-resource'

const toast = useToast()
const route = useRoute()
const badgeId = route.params.badgeId

const jsonDl = ref({})

const { data: payload, status } = useLazyAsyncData(() => getApiAdminBadgesUuid(badgeId))

watchEffect(() => {
    if (payload?.value?.status == 200)
        jsonDl.value = payload.value.data.jsonPayload
    if (payload?.value?.status == 404)
        return navigateTo('/badges')
            .then(_ => toast.error({ title: 'Error al cargar la credencial' }))
})

const sanitizedHtml = computed(() => {
    const rawHtml = marked.parse(payload?.value?.data?.criteriaMd || '')
    return DOMPurify.sanitize(rawHtml)
})

const copyJson = () => copy(JSON.stringify(jsonDl.value), 'JSON-LD copiado')
</script>

<template>
    <div>
        <NuxtLink to="/badges" class="link inline-flex items-center gap-1 text-sm mb-6">
            <Icon name="material-symbols:arrow-back" class="text-lg" />
            Volver a mis credenciales
        </NuxtLink>

        <!-- Loading -->
        <div v-if="status !== 'success'" class="flex flex-col gap-6">
            <div class="card card-pad flex flex-col lg:flex-row gap-8 items-center">
                <div class="skeleton h-56 w-56 rounded-3xl shrink-0"></div>
                <div class="flex-1 w-full space-y-3">
                    <div class="skeleton h-8 w-1/2"></div>
                    <div class="skeleton h-4 w-full"></div>
                    <div class="skeleton h-4 w-3/4"></div>
                </div>
            </div>
        </div>

        <template v-else>
            <!-- Header card -->
            <div class="card card-pad relative flex flex-col lg:flex-row gap-8 items-center lg:items-start">
                <div class="rounded-[1.5rem] border border-line-strong bg-surface-2 p-2 shrink-0">
                    <img :src="payload.data.jsonPayload.image" :alt="payload.data.name" class="h-56 w-56 rounded-[calc(1.5rem-0.5rem)] object-cover" />
                </div>
                <div class="flex-1 flex flex-col gap-4 text-center lg:text-left">
                    <span class="badge badge-teal self-center lg:self-start">
                        <Icon name="material-symbols:workspace-premium" class="text-base" />
                        Credencial
                    </span>
                    <h1 class="font-display text-3xl md:text-4xl font-bold tracking-tight">{{ payload.data.name }}</h1>
                    <p class="text-ink-soft text-balance">{{ payload.data.description }}</p>
                    <NuxtLink :to="`/organizations/${payload.data.issuer.id}`"
                        class="inline-flex items-center gap-2 self-center lg:self-start text-sm font-semibold text-teal hover:text-teal-strong">
                        <Icon name="material-symbols:domain" class="text-lg" />
                        {{ payload.data.issuer.name }}
                    </NuxtLink>
                </div>
                <div class="lg:self-center shrink-0">
                    <OnlyMembers :issuer-uuid="payload.data.issuer.id">
                        <BadgesEmitBadge :issuer-id="payload.data.issuer.id" :badge-id="badgeId" />
                    </OnlyMembers>
                </div>
            </div>

            <!-- Revocation (OWNER / ADMIN) -->
            <div class="mt-4 flex justify-end">
                <OnlyMembers :issuer-uuid="payload.data.issuer.id" :roles="[MemberRole.OWNER, MemberRole.ADMIN]">
                    <BadgesRevokeBadge :issuer-uuid="payload.data.issuer.id" :badge-class-uuid="badgeId" />
                </OnlyMembers>
            </div>

            <div class="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-6">
                <!-- Metadata -->
                <div class="card card-pad">
                    <div class="flex items-center justify-between mb-4">
                        <h2 class="font-display text-lg font-bold">Metadata</h2>
                        <div class="flex gap-2">
                            <button class="btn btn-outline btn-sm" @click="copyJson" aria-label="Copiar JSON-LD">
                                <Icon name="material-symbols:content-copy" class="text-lg" />
                            </button>
                            <NuxtLink external :to="jsonDl.id" target="_blank" rel="noopener" class="btn btn-outline btn-sm" aria-label="Abrir JSON-LD">
                                <Icon name="material-symbols:open-in-new" class="text-lg" />
                            </NuxtLink>
                        </div>
                    </div>
                    <div class="flex flex-col gap-4">
                        <div class="field">
                            <span class="field-label font-mono text-xs uppercase tracking-wider">@context</span>
                            <input type="text" class="input font-mono text-xs" readonly :value="jsonDl['@context']" />
                        </div>
                        <div class="grid sm:grid-cols-2 gap-4">
                            <div class="field">
                                <span class="field-label font-mono text-xs uppercase tracking-wider">Type</span>
                                <input type="text" class="input font-mono text-xs" readonly :value="jsonDl.type" />
                            </div>
                            <div class="field">
                                <span class="field-label font-mono text-xs uppercase tracking-wider">ID canónico</span>
                                <input type="text" class="input font-mono text-xs" readonly :value="badgeId" />
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Criteria -->
                <div class="card card-pad">
                    <h2 class="font-display text-lg font-bold mb-4">Criterios</h2>
                    <div class="prose prose-sm max-w-none dark:prose-invert" :class="''" v-html="sanitizedHtml"></div>
                </div>
            </div>
        </template>
    </div>
</template>

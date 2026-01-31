<script setup>
import { getApiAdminMeAssertions } from '~/services/admin-resource/admin-resource';

const { params, setParam } = useParams('getApiAdminMeAssertions' + 'Params', { page: 1, sort: 'name' })
const { data: paginatedAssertions, status, refresh } = useLazyAsyncData(() => getApiAdminMeAssertions(params.value),
    { transform: (data) => data.data })

const open = (assertion) => {
    const features = {
        toolbar: false,
        location: false,
        menubar: false,
        width: 800,
        height: 600,
        scrollbars: true
    }
    window.open(
        `/api/v2/assertions/${assertion.id}`,
        assertion.badgeClass.name,
        Object.keys(features)
            .map(key => `${key}=${features[key]}`)
            .join(',')
    )
}

const assertionPublicUrl = (assertion) =>
    `${window.location.origin}/api/v2/assertions/${assertion.id}`

const generateShareOnLinkedinUrl = (assertion) => {
    const date = new Date(assertion.issuedOn);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const params = new URLSearchParams({
        startTask: 'CERTIFICATION_NAME',
        name: assertion.badgeClass.name,
        organizationName: assertion.badgeClass.issuer.name,
        issueYear: year.toString(),
        issueMonth: month.toString(),
        certId: assertion.id,
        certUrl: assertionPublicUrl(assertion),
    })
    return `https://www.linkedin.com/profile/add?${params.toString()}`
}

const generateShareOnTwitterUrl = (assertion) => {
    const text = encodeURIComponent(`¡Acabo de obtener la insignia "${assertion.badgeClass.name}" en ${assertion.badgeClass.issuer.name}! 🚀`);
    const url = encodeURIComponent(assertionPublicUrl(assertion));
    const hashtags = "Java,Quarkus,OpenBadges";
    return `https://twitter.com/intent/tweet?text=${text}&url=${url}&hashtags=${hashtags}`;
};

const prevPage = _ => setParam('page', params.value.page - 1)
const nextPage = _ => setParam('page', params.value.page + 1)
watch(params, _ => refresh())
</script>

<template>
    <div v-if="status != 'success'" class="grid place-content-center h-screen">
        <span class="loading loading-ring loading-xl"></span>
    </div>
    <div v-else class="card bg-base-200 shadow">
        <div class="card-body">
            <h3 class="card-title">Credenciales</h3>
            <div class="divider m-0"></div>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-6 gap-3">
                <div v-for="assertion in paginatedAssertions.data" :key="assertion.id"
                    class="card shadow hover:shadow-xl transition">
                    <div class="card-body p-2">
                        <button @click="open(assertion)">
                            <img :src="assertion.badgeClass.jsonPayload.image" :alt="assertion.badgeClass.name">
                        </button>
                        <div class="flex gap-2">
                            <button @click="openModal(`share-dialog-${assertion.id}`)"
                                class="flex-1 btn btn-xs btn-primary btn-block">
                                <Icon name="material-symbols:share" class="text-lg" />
                            </button>
                            <dialog :id="`share-dialog-${assertion.id}`" class="modal">
                                <div class="modal-box">
                                    <h3 class="text-lg font-bold">{{ assertion.badgeClass.name }}</h3>
                                    <div class="divider m-0"></div>
                                    <div class="grid grid-cols-2 gap-2">
                                        <a :href="generateShareOnLinkedinUrl(assertion)" target="_blank"
                                            class="btn btn-block btn-primary">
                                            Linkedin
                                        </a>
                                        <a :href="generateShareOnTwitterUrl(assertion)" target="_blank"
                                            class="btn btn-block btn-primary">
                                            Twitter
                                        </a>
                                        <button @click="copy(assertionPublicUrl(assertion), 'Link publico copiado')"
                                            class="btn btn-block btn-primary">
                                            Link Publico
                                        </button>
                                    </div>
                                </div>
                                <form method="dialog" class="modal-backdrop">
                                    <button>close</button>
                                </form>
                            </dialog>
                            <a :href="`/api/admin/assertions/${assertion.id}/bakedimage`" target="_blank"
                                class="flex-1 btn btn-xs btn-secondary btn-block">
                                <Icon name="material-symbols:download" class="text-lg" />
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="grid place-items-center mt-4">
                <div class="join" v-if="status == 'success'">
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedAssertions?.meta?.prevPage }"
                        @click="prevPage">«</button>
                    <button class="join-item btn btn-active cursor-auto">Page {{
                        paginatedAssertions?.meta?.currentPage }}</button>
                    <button class="join-item btn" :class="{ 'btn-disabled': !paginatedAssertions?.meta?.nextPage }"
                        @click="nextPage">»</button>
                </div>
            </div>
        </div>
    </div>
</template>
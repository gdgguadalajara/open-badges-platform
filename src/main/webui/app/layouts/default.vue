<script setup>
const { me } = useAuth()
</script>

<template>
    <div class="navbar bg-base-200 shadow-sm px-10">
        <div class="flex-1">
            <NuxtLink to="/" class="btn btn-ghost text-xl p-2">
                Open Badges Platform
            </NuxtLink>
        </div>
        <div class="flex-none">
            <NuxtLink :to="'/login?r=' + $route.path" v-if="me == null" class="btn btn-primary">
                Iniciar Sesión
            </NuxtLink>
            <div v-else class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-primary m-1">
                    {{ me?.account?.fullName }}
                </div>
                <ul tabindex="-1" class="dropdown-content menu bg-base-100 rounded-box z-1 w-52 p-2 shadow-sm">
                    <OnlySuperUsers>
                        <li>
                            <NuxtLink to="/organizations">
                                <Icon name="material-symbols:domain" class="text-2xl" />
                                <span class="is-drawer-close:hidden">Organizaciones</span>
                            </NuxtLink>
                        </li>
                    </OnlySuperUsers>
                    <li v-if="me?.memberships?.length > 0">
                        <NuxtLink to="/hubs">
                            <Icon name="material-symbols:hub" class="text-2xl" />
                            <span class="is-drawer-close:hidden">Mis organizaciones</span>
                        </NuxtLink>
                    </li>
                    <li>
                        <NuxtLink :to="`/badges`">
                            <Icon name="material-symbols:workspace-premium" class="text-2xl" />
                            Mis credenciales
                        </NuxtLink>
                    </li>
                    <li></li>
                    <li>
                        <NuxtLink to="/logout">
                            <Icon name="material-symbols:exit-to-app-rounded" class="text-2xl" />
                            <span class="is-drawer-close:hidden">Cerrar Sesión</span>
                        </NuxtLink>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="px-3 lg:px-10 py-10">
        <slot></slot>
    </div>
    <footer class="footer footer-horizontal footer-center bg-base-200 text-base-content rounded p-10">
        <nav class="grid grid-flow-col gap-4">
            <NuxtLink to="/terms" class="link link-hover">
                Términos de Uso
            </NuxtLink>
            <NuxtLink to="/privacy" class="link link-hover">
                Aviso de Privacidad
            </NuxtLink>
            <NuxtLink to="https://github.com/gdgguadalajara/open-badges-platform" target="_blank" external
                class="link link-hover">
                Github
            </NuxtLink>
        </nav>
        <nav>
            <NuxtLink to="https://gdg.community.dev/gdg-guadalajara/" target="_blank" external class="link link-hover">
                Hecho con 💙 por la comunidad de GDG Guadalajara
            </NuxtLink>
        </nav>
    </footer>
</template>
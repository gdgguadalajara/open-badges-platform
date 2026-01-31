export default defineNuxtRouteMiddleware(async (to, from) => {
    const { fetchMe, meResponse } = useAuth();
    const publicRoutes = ['/', '/login']
    const isPublicRoute = publicRoutes.includes(to.path)
    if (!isPublicRoute) {
        await fetchMe()

        if (meResponse.value?.status && meResponse.value?.status >= 300 && meResponse.value?.status <= 399)
            return navigateTo('/login')
        if (
            meResponse.value?.status && meResponse.value?.status >= 400
            && meResponse.value?.status <= 499
            && to.path != '/register'
        )
            return navigateTo('/register')
    }
});
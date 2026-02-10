export default defineNuxtRouteMiddleware(async (to, from) => {
    const { fetchMe, meResponse } = useAuth();
    const publicRouteNames = [
        'index',
        'login',
        'terms',
        'privacy',
        'badges-badgeId',
        'organizations-id'
    ]
    
    const isPublicRoute = publicRouteNames.includes(to.name as string)
    await fetchMe()
    if (!isPublicRoute) {
        if (meResponse.value?.status && meResponse.value?.status >= 300 && meResponse.value?.status <= 399)
            return navigateTo('/login')
        if (
            meResponse.value?.status && meResponse.value?.status >= 400
            && meResponse.value?.status <= 499
            && (to.path != '/register' && to.path != '/logout')
        )
            return navigateTo('/register')
    }
});
import type { MeResponse } from "~/models"
import { getApiAuthMe, type getApiAuthMeResponse } from "~/services/authentication-resource/authentication-resource"

export const useAuth = () => {
    const meResponse = useState<getApiAuthMeResponse | null>('auth-response', () => null)
    const me = useState<MeResponse | null>('auth-user', () => null)
    const lastCheck = useState<number>('lastAuthCheck', () => 0)
    const currentFlight = useState<Promise<any> | null>('auth-promise', () => null)

    const CHECK_INTERVAL = 30 * 60 * 1000

    const fetchMe = async () => {
        const now = Date.now()

        if (me.value && (now - lastCheck.value < CHECK_INTERVAL))
            return me.value

        if (currentFlight.value)
            return currentFlight.value

        currentFlight.value = (async () => {
            try {
                const payload = await getApiAuthMe()
                meResponse.value = payload

                if (payload.status === 200) {
                    me.value = payload.data
                    lastCheck.value = Date.now()
                    return payload.data
                }
                return null
            } catch (e) {
                meResponse.value = { data: null, status: 302 } as any
                return null
            } finally {
                currentFlight.value = null
            }
        })()

        return currentFlight.value
    }

    return { me, meResponse, fetchMe }
}
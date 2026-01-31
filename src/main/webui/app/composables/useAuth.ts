import type { MeResponse } from "~/models";
import { getApiAuthMe } from "~/services/authentication-resource/authentication-resource";

export const useAuth = () => {
    const me = useState<MeResponse | null>('auth-user', () => null);
    const isLoading = useState<boolean | null>('auth-user-loading', () => false);
    const fetchMe = () => {
        if (isLoading.value == true) return
        isLoading.value = true;
        getApiAuthMe()
            .then(({ data, status }) => status == 200 ? Promise.resolve(data) : Promise.reject())
            .then(data => me.value = data)
            .finally(() => isLoading.value = false)
    }
    if (me.value == null) fetchMe()
    return { me, fetchMe }
}
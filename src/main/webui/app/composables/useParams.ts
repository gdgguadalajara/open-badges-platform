export default function (key: string, initialParas: { [x: string]: any }) {
    const filters = useState(key, () => (initialParas ?? {}))

    const setParam = (paramName: string | object, value: any) => {
        if (typeof paramName == 'object')
            filters.value = { ...filters.value, ...paramName }
        filters.value[paramName as string] = value
    }

    const filteredParams = computed(() => Object.fromEntries(
        Object.entries(filters.value)
            .filter(e => isNotEmpty(e[1]))))

    const customFilters = computed(() => Object.entries(filteredParams.value)
        .filter(p =>
            p[0] != 'size'
            && p[0] != 'page'
            && p[0] != 'sort'))

    const clearParam = (param: string) => setParam(param, null)

    return {
        params: filteredParams,
        setParam,
        clearParam,
        allParams: filters,
        customFilters,
    }
}

function isNotEmpty(value: any) {
    if (!value) return false
    if (Array.isArray(value) && value.length === 0) return false
    if (typeof value === 'object' && Object.keys(value).length === 0) return false;
    return true;
}
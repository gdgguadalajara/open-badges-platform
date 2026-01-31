export default function (copyPayload: string, message: string) {
    navigator.clipboard.writeText(copyPayload)
        .then(_ => useToast().info({ title: message }))
}
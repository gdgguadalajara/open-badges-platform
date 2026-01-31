export default function (name: string) {
    (document.getElementById(name) as HTMLDialogElement)?.showModal()
}
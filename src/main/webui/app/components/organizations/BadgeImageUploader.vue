<script setup>
const emit = defineEmits(['update:image'])
const previewUrl = ref(null)
const error = ref(null)
const fileInput = ref(null)

const MAX_SIZE_MB = 2
const MAX_DIMENSION = 500

const onFileChange = (event) => {
    error.value = null
    const file = event.target.files?.[0]
    if (!file) return
    const fileSizeMB = file.size / 1048576
    if (fileSizeMB > MAX_SIZE_MB) {
        error.value = `Archivo muy pesado. Máximo ${MAX_SIZE_MB}MB.`
        resetInput()
        return
    }
    const reader = new FileReader()
    reader.onload = (e) => {
        const img = new Image()
        img.src = e.target?.result
        img.onload = () => {
            if (img.width !== img.height || img.width > MAX_DIMENSION) {
                error.value = "Dimensiones inválidas (Máx 500x500 y cuadrada)."
                resetInput()
                return
            }
            previewUrl.value = img.src
            emit('update:image', img.src)
        }
    }
    reader.readAsDataURL(file)
}

const resetInput = () => {
    previewUrl.value = null
    emit('update:image', null)
    if (fileInput.value) fileInput.value.value = ''
}
</script>

<template>
    <div class="flex flex-col items-center">
        <label
            class="relative flex flex-col items-center justify-center w-64 h-64 border-2 border-dashed rounded-xl cursor-pointer overflow-hidden border-gray-300">
            <img v-if="previewUrl" :src="previewUrl" class="absolute inset-0 object-cover w-full h-full" />
            <div v-else class="flex flex-col items-center justify-center pt-5 pb-6">
                <span class="material-symbols-outlined text-gray-400 text-5xl mb-2">Imagen</span>
                <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">PNG cuadrado</p>
                <p class="text-[10px] text-gray-400 mt-1">Máx. 500x500px (2MB)</p>
            </div>
            <input ref="fileInput" type="file" class="hidden" accept="image/png" @change="onFileChange" />
        </label>
        <p v-if="error" class="mt-2 text-xs text-red-500 font-bold uppercase">{{ error }}</p>
    </div>
</template>
<script setup>
import json from '~/assets/json/json-dl-assertion-example.json'
</script>

<template>
    <section
        :class="['grid place-content-center h-125 bg-base-100 shadow',
            'bg-[linear-gradient(to_right,#f0f0f0_1px,transparent_5px),linear-gradient(to_bottom,#f0f0f0_1px,transparent_5px)] bg-size-[50px_50px]']">
        <div class="max-w-sm md:max-w-2xl lg:max-w-4xl mb-6 text-center">
            <h2 class="text-4xl sm:text-7xl font-bold font-display tracking-tight">
                Confianza para tus
            </h2>
            <h3
                class="pb-2 text-4xl sm:text-7xl font-bold bg-clip-text text-transparent bg-linear-to-r from-primary to-secondary">
                Credenciales Digitales
            </h3>
            <p class="mt-4 sm:text-lg text-slate-600">
                Emisión, verificación y gestión de insignias digitales con estándares Open Badges para
                organizaciones e instituciones que demandan máxima integridad de datos.
            </p>
        </div>
    </section>

    <section class="mt-20 max-w-7xl mx-auto">
        <div class="grid md:grid-cols-3 gap-3">
            <div class="card bg-secondary shadow hover:shadow-xl transition">
                <div class="card-body text-secondary-content">
                    <h3 class="card-title text-2xl">
                        <Icon name="material-symbols:lock" class="text-2xl" />
                        Seguridad y Cumplimiento
                    </h3>
                    <p class="text-sm md:text-base">
                        Protección avanzada mediante metadatos inyectados (Baking) y hashing SHA-256.
                        Garantizamos que cada insignia sea una prueba inmutable, segura y soberana de tus logros.
                    </p>
                </div>
            </div>

            <div class="card bg-secondary shadow hover:shadow-xl transition">
                <div class="card-body text-secondary-content">
                    <h3 class="card-title text-2xl">
                        <Icon name="material-symbols:language" class="text-2xl" />
                        Interoperabilidad Global
                    </h3>
                    <p class="text-sm md:text-base">
                        Basada en el estándar Open Badges. Tus insignias no están atrapadas; son portátiles y
                        compatibles con backpacks globales, LinkedIn y cualquier plataforma de verificación W3C.
                    </p>
                </div>
            </div>

            <div class="card bg-secondary shadow hover:shadow-xl transition">
                <div class="card-body text-secondary-content">
                    <h3 class="card-title text-2xl">
                        <Icon name="material-symbols:verified" class="text-2xl" />
                        Validación en Tiempo Real
                    </h3>
                    <p class="text-sm md:text-base">
                        Eliminamos la burocracia en la verificación de competencias. Cualquier tercero puede
                        confirmar la legitimidad de tus logros al instante, transformando certificados estáticos en
                        activos digitales dinámicos.
                    </p>
                </div>
            </div>
        </div>
    </section>

    <section class="mt-20 max-w-7xl mx-auto px-4">
        <div class="flex flex-col md:flex-row items-center gap-5">

            <div class="flex-1 flex flex-col gap-5 w-full">
                <div class="card bg-primary shadow hover:shadow-xl transition">
                    <div class="card-body text-primary-content">
                        <h3 class="card-title text-2xl">
                            <Icon name="material-symbols:code" class="text-2xl" />
                            JSON-LD & Linked Data
                        </h3>
                        <p class="text-sm md:text-base">
                            Implementamos datos vinculados para que la información del logro sea legible por máquinas,
                            facilitando su indexación y reconocimiento global inmediato.
                        </p>
                    </div>
                </div>

                <div class="card bg-primary shadow hover:shadow-xl transition">
                    <div class="card-body text-primary-content">
                        <h3 class="card-title text-2xl">
                            <Icon name="material-symbols:shield" class="text-2xl" />
                            Autenticidad portable
                        </h3>
                        <p class="text-sm md:text-base">
                            Validación criptográfica que reside en la propia insignia, permitiendo certificar su
                            veracidad en cualquier plataforma sin intermediarios.
                        </p>
                    </div>
                </div>
            </div>

            <div class="flex-1 min-w-0 w-full">
                <div class="flex items-center gap-3 mb-4 px-4">
                    <Icon name="material-symbols:code-blocks" class="text-2xl" />
                    <h3 class="font-mono text-sm tracking-widest uppercase">Metadata Verificable (JSON-LD)</h3>
                </div>

                <div class="border border-slate-800 rounded-xl overflow-hidden bg-slate-100">
                    <div class="prose prose-invert prose-slate max-w-full overflow-x-auto">
                        <div class="bg-primary px-6 py-2 border-b border-slate-800 flex justify-between items-center">
                            <div class="flex gap-1.5">
                            </div>
                            <span
                                class="text-[10px] font-mono text-primary-content uppercase tracking-widest font-bold">
                                Open Badges
                            </span>
                        </div>

                        <pre
                            class="my-0! bg-transparent! p-4! text-xs md:text-sm overflow-x-auto scrollbar-thin scrollbar-thumb-slate-700"><code class="language-json text-slate-800">{{ json }}</code></pre>
                    </div>
                </div>

                <p class="mt-4 text-slate-500 text-xs italic px-4">
                    * Este bloque de datos vinculados permite que la insignia sea indexada y validada.
                </p>
            </div>
        </div>
    </section>
</template>
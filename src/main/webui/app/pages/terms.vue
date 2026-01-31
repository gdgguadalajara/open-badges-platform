<template>
    <div class="card bg-base-200 shadow">
        <div class="card-body">
            <h2 id="-t-rminos-y-condiciones-de-uso" class="text-2xl font-bold">Términos y Condiciones de Uso</h2>
            <h3 id="1-definiciones-y-roles">1. Definiciones y Roles</h3>
            <p>Para la transparencia de esta plataforma, se establecen los siguientes roles:</p>
            <ul>
                <li><strong>Desarrollador (GDG Guadalajara):</strong> Autor y mantenedor del código fuente original.
                    Provee la
                    lógica de software de manera gratuita y abierta.</li>
                <li><strong>Anfitrión de Infraestructura (Host):</strong> Entidad independiente que despliega, opera y
                    gestiona
                    esta
                    instancia específica. Es el responsable de levantar las credenciales de <strong>OAuth2</strong> y
                    asegurar
                    la
                    persistencia de los datos.</li>
                <li><strong>Organización Emisora (Issuer):</strong> Entidad registrada que utiliza el sistema para
                    otorgar
                    acreditaciones digitales.</li>
                <li><strong>Recipiente:</strong> Usuario final que recibe y gestiona sus insignias.</li>
            </ul>
            <h3 id="2-naturaleza-del-software-y-deslinde-de-garant-as">2. Naturaleza del Software y Deslinde de
                Garantías</h3>
            <p>El software se proporciona <strong>&quot;tal cual&quot; (as-is)</strong>.</p>
            <ul>
                <li><strong>GDG Guadalajara</strong>, en su calidad de Desarrollador, no otorga garantías sobre la
                    idoneidad,
                    seguridad o ausencia de errores en el código.</li>
                <li>El uso de la plataforma no crea una relación comercial ni de prestación de servicios profesionales
                    entre las
                    partes.</li>
            </ul>
            <h3 id="3-responsabilidad-del-anfitri-n-host-">3. Responsabilidad del Anfitrión (Host)</h3>
            <p>El <strong>Host</strong> es el único responsable de la operación técnica de esta instancia, incluyendo:
            </p>
            <ul>
                <li>La correcta configuración y resguardo de las credenciales de autenticación (Client IDs y Secrets).
                </li>
                <li>La seguridad lógica de los servidores y bases de datos.</li>
                <li>La disponibilidad del servicio y el soporte técnico a las Organizaciones Emisoras.</li>
            </ul>
            <h3 id="4-responsabilidad-de-la-informaci-n">4. Responsabilidad de la Información</h3>
            <ul>
                <li><strong>Veracidad:</strong> La legitimidad de las insignias y el cumplimiento de sus criterios son
                    responsabilidad exclusiva de la <strong>Organización Emisora</strong>. Ni el Desarrollador ni el
                    Host
                    validan
                    las competencias acreditadas por terceros.</li>
                <li><strong>Privacidad Técnica:</strong> Se utiliza el estándar <strong>Open Badges</strong>, lo que
                    implica que
                    el
                    correo del recipiente se procesa mediante un <strong>hash SHA-256</strong> para proteger su
                    identidad en los
                    metadatos públicos de la insignia.</li>
            </ul>
            <h3 id="5-propiedad-y-portabilidad">5. Propiedad y Portabilidad</h3>
            <p>El Recipiente posee una licencia perpetua para compartir y descargar su insignia &quot;horneada&quot;
                (con
                metadatos
                inyectados). El Emisor se reserva el derecho de <strong>revocar</strong> una insignia únicamente en
                casos de
                error
                administrativo, fraude probado o violación a los códigos de conducta de la comunidad.</p>
            <h3 id="6-modificaciones-y-jurisdicci-n">6. Modificaciones y Jurisdicción</h3>
            <p>El Host se reserva el derecho de actualizar estos términos para adaptarlos a cambios técnicos o legales
                en su
                jurisdicción.</p>
            <p>El uso continuo de la plataforma tras el login implica la aceptación de la versión vigente de
                estos
                Términos y Condiciones.</p>
        </div>
    </div>
</template>
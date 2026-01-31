<template>
    <div class="card bg-base-200 shadow">
        <div class="card-body">
            <h1 id="aviso-de-privacidad" class="text-2xl font-bold">Aviso de Privacidad</h1>
            <h3 id="1-responsable-del-tratamiento">1. Responsable del Tratamiento</h3>
            <p>El <strong>Anfitrión de Infraestructura (Host)</strong> de esta instancia es el responsable del
                tratamiento,
                resguardo y administración de sus datos personales. El <strong>Desarrollador (GDG Guadalajara)</strong>
                provee
                el código fuente pero no tiene acceso, propiedad ni control sobre la base de datos o las credenciales de
                esta
                implementación específica.</p>
            <h3 id="2-datos-personales-recabados">2. Datos Personales Recabados</h3>
            <p>Recabamos la información necesaria para gestionar sus acreditaciones digitales:</p>
            <ul>
                <li><strong>Vía Google OAuth2:</strong> Nombre completo, dirección de correo electrónico y fotografía de
                    perfil.
                </li>
                <li><strong>Vía Organización Emisora:</strong> Dirección de correo electrónico (proporcionada por los
                    administradores de la organización para emitirle una insignia).</li>
                <li><strong>Uso de Cookies y Tecnologías Similares:</strong> Esta plataforma utiliza cookies técnicas
                    con el único fin de gestionar la autenticación del usuario a través de Google OAuth2 y mantener la
                    seguridad de la sesión. Estas cookies son esenciales para el funcionamiento del servicio y no
                    recopilan información para fines publicitarios o de rastreo de comportamiento en sitios terceros.
                </li>
            </ul>
            <h3 id="3-finalidades-del-tratamiento">3. Finalidades del Tratamiento</h3>
            <p>Sus datos serán utilizados exclusivamente para las siguientes finalidades:</p>
            <ul>
                <li><strong>Identidad Digital:</strong> Generar y asignar insignias digitales bajo el estándar
                    <strong>Open
                        Badges</strong>.
                </li>
                <li><strong>Notificaciones:</strong> Enviar avisos por correo electrónico sobre la recepción de nuevas
                    insignias
                    o actualizaciones de su cuenta.</li>
                <li><strong>Verificación:</strong> Permitir que terceros validen la autenticidad de sus logros mediante
                    una URL
                    pública de verificación (siempre que la insignia esté configurada como pública).</li>
            </ul>
            <h3 id="4-privacidad-t-cnica-y-seguridad-hashing-">4. Privacidad Técnica y Seguridad (Hashing)</h3>
            <p>Para proteger su identidad, aplicamos el principio de <strong>Privacidad por Diseño</strong>:</p>
            <ul>
                <li>La dirección de correo electrónico se procesa mediante una función criptográfica de un solo sentido
                    (<strong>SHA-256</strong>) antes de ser inyectada en los metadatos de la imagen y en el JSON-LD de
                    verificación.</li>
                <li>Esto garantiza que su correo electrónico no sea legible en texto plano para sistemas externos de
                    minería de
                    datos, cumpliendo con los estándares internacionales de seguridad de credenciales.</li>
            </ul>
            <h3 id="5-transferencia-de-datos">5. Transferencia de Datos</h3>
            <p>No vendemos, alquilamos ni compartimos sus datos personales con terceros para fines comerciales.</p>
            <ul>
                <li><strong>OAuth2:</strong> La comunicación de datos ocurre directamente entre el proveedor de
                    identidad
                    (Google) y el <strong>Host</strong> de esta instancia a través de las credenciales gestionadas por
                    este
                    último.</li>
                <li><strong>Acceso Técnico:</strong> El <strong>Host</strong> tiene acceso técnico a la infraestructura
                    con
                    fines exclusivos de mantenimiento, soporte y operación del sistema.</li>
            </ul>
            <h3 id="6-derechos-arco">6. Derechos ARCO</h3>
            <p>Usted tiene derecho a conocer qué datos tenemos de usted, así como a solicitar su corrección o
                eliminación
                definitiva (<strong>Acceso, Rectificación, Cancelación y Oposición</strong>).</p>
            <ul>
                <li>Para ejercer estos derechos, debe enviar una solicitud al correo de contacto proporcionado por el
                    <strong>Host</strong> de esta instancia o a través de los canales de soporte de la organización
                    emisora.
                </li>
            </ul>
        </div>
    </div>
</template>
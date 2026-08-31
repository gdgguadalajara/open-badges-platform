# SPEC — Revocación de Badges Entregadas

> **Estado:** Propuesta
> **Módulos involucrados:** `assertion`, `issuer`, `membership`, `storage`
> **Rama sugerida:** `feat/revoke-assertions`

---

## 1. Contexto y Problema

Las organizaciones emiten insignias digitales (Assertions) que pueden necesitar **revocarse**
post-emisión. Los términos y condiciones de la plataforma (ver `SPECS/../README.md` y
`Términos de Uso`) establecen que el Emisor puede revocar una insignia únicamente en casos de:

- error administrativo en la emisión,
- fraude probado,
- o violación a los códigos de conducta de la comunidad.

El modelo de datos **ya soporta** el estado (`Assertion.isRevoked`, `Assertion.revocationReason`),
pero **no existe** funcionalidad ni endpoint para revocar. Las assertion revocadas
actualmente no se pueden marcar, y el contenido público (HTML/JSON-LD) no refleja el estado.

Este SPEC propone la funcionalidad completa para que los administradores de una organización
**revoquen badges ya entregadas**, con trazabilidad del motivo y reflejo correcto en la
verificación pública.

---

## 2. Objetivos

1. Permitir a `OWNER` y `ADMIN` de una organización revocar una o más assertions emitidas por ella.
2. Registrar el **motivo de revocación** (`revocationReason`) de forma obligatoria.
3. Reflejar el estado revocado en los **metadatos públicos** (JSON-LD) y en la **página HTML**.
4. Impedir revocar assertions de otras organizaciones (validación por membresía).
5. Permitir **des-revocar** (restaurar) una assertion revocada por error, conservando trazabilidad.
6. Permitir la **revocación masiva** de todas las assertions de una credencial, o de un
   subconjunto seleccionado, en una sola operación atómica.

---

## 3. Roles y Permisos

| Rol      | Revocar | Des-revocar | Ver motivo | Emitir |
| -------- | ------- | ----------- | ---------- | ------ |
| `OWNER`  | ✅      | ✅          | ✅         | ✅     |
| `ADMIN`  | ✅      | ✅          | ✅         | ❌ (solo OWNER) |

Se reutiliza `@OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })` + `OrgSecurityFilter`,
validando que el `issuerUuid` de la ruta corresponda a la membresía del usuario autenticado.

---

## 4. Modelo de Datos (ya existente)

```
Assertion
 ├── isRevoked         boolean  (default false)
 ├── revocationReason  varchar(255)
 ├── account           → Account (recipiente reclamado) | null
 ├── isPublic          boolean
 ├── badgeClass        → BadgeClass → Issuer
 └── issuedOn          Instant
```

> No requiere migración de BD: las columnas `isRevoked` y `revocationReason` ya existen
> en `V1.0.0__Initial_Schema.sql`.

---

## 5. Contratos de API

### 5.1 Revocar una assertion

```
PATCH /api/v2/issuers/{issuerUuid}/badges/{badgeClassUuid}/assertions/{assertionUuid}/revoke
Content-Type: application/json
```

> El endpoint vive en `IssuerAssertionResource`, por lo que la ruta conserva el contexto
> de badge (`{badgeClassUuid}`) además del `issuerUuid`. `OrgSecurityFilter` valida la
> membresía sobre `issuerUuid` y el caso de uso valida la pertenencia de la assertion.

**Auth:** `@Authenticated` + `@OrgRole({ OWNER, ADMIN })`

**Request**

```json
{
  "reason": "Emisión duplicada por error administrativo"
}
```

**Response `200 OK`**

```json
{
  "assertionId": "edb7dd30-690a-4901-beb6-21af30a80ba0",
  "isRevoked": true,
  "revocationReason": "Emisión duplicada por error administrativo",
  "revokedAt": "2026-08-29T12:00:00Z"
}
```

**Errores:**

| Código | Caso                                             |
| ------ | ------------------------------------------------ |
| `400`  | `reason` vacío o en blanco (validación `@NotBlank`) |
| `404`  | Assertion no encontrada o de otra organización     |
| `409`  | La assertion ya está revocada                     |

### 5.2 Des-revocar una assertion

```
PATCH /api/v2/issuers/{issuerUuid}/badges/{badgeClassUuid}/assertions/{assertionUuid}/unrevoke
```

**Auth:** `@Authenticated` + `@OrgRole({ OWNER, ADMIN })`

**Response `200 OK`**

```json
{
  "assertionId": "edb7dd30-690a-4901-beb6-21af30a80ba0",
  "isRevoked": false,
  "revocationReason": null
}
```

**Errores:**

| Código | Caso                                  |
| ------ | ------------------------------------- |
| `404`  | Assertion no encontrada o de otra org |
| `409`  | La assertion no está revocada         |

### 5.3 Revocación masiva por badge

```
PATCH /api/v2/issuers/{issuerUuid}/badges/{badgeClassUuid}/revoke
Content-Type: application/json
```

**Auth:** `@Authenticated` + `@OrgRole({ OWNER, ADMIN })`

Revoca **todas las assertions** de una credencial que cumplan un filtro, o un subconjunto
explícito de `assertionIds`. Ambos modos comparten el mismo motivo.

**Request (modo "todos"):**

```json
{
  "reason": "Credencial descontinuada",
  "assertionIds": null
}
```

**Request (modo "selección"):**

```json
{
  "reason": "Emisión irregular detectada",
  "assertionIds": ["uuid-1", "uuid-2", "uuid-3"]
}
```

**Response `200 OK`**

```json
{
  "issuerId": "2a2c6b3c-...",
  "badgeClassId": "f1f21e11-...",
  "reason": "Credencial descontinuada",
  "total": 3,
  "revoked": 3,
  "skipped": 0,
  "revokedAt": "2026-08-29T12:00:00Z",
  "assertionIds": ["uuid-1", "uuid-2", "uuid-3"]
}
```

**Semántica:**

- `assertionIds == null` (o vacío) → se revocan **todas** las assertions **no revocadas** del badge.
- `assertionIds` con valores → solo esas, siempre que pertenezcan al `badgeClassUuid` y a la organización.
- Las assertions **ya revocadas** se cuentan en `skipped` y no se tocan (idempotencia por lote).
- Los `assertionIds` que no existan o no pertenezcan al badge se ignoran y se cuentan en `skipped`.
- `total` = assertions consideradas, `revoked` = aplicadas, `skipped` = `total - revoked`.

**Errores:**

| Código | Caso                                             |
| ------ | ------------------------------------------------ |
| `400`  | `reason` vacío o en blanco (validación `@NotBlank`) |
| `400`  | Ninguna assertion elegible encontrada (lote vacío) |
| `404`  | BadgeClass no encontrado o de otra organización   |

**Errores parciales:** no se usa; la operación es atómica por lote en una transacción
(si una falla, no se aplica ninguna). Los casos por-assertion se reportan en `skipped`.

---

## 6. JSON-LD y HTML público

### 6.1 JSON-LD (`AssertionJsonLd`)

Según el estándar **Open Badges 2.0**, una assertion revocada debe reflejarse en los metadatos.
Se agregan los campos al record `AssertionJsonLd`:

```json
{
  "@context": "https://w3id.org/openbadges/v2",
  "id": "urn:uuid:...",
  "type": "Assertion",
  "revoked": true,
  "revocationReason": "Emisión duplicada por error administrativo",
  "...": "..."
}
```

Cambio en `AssertionJsonLd`:

```java
public record AssertionJsonLd(
        @JsonProperty("@context") String context,
        String id,
        String type,
        Recipient recipient,
        String badge,
        String issuedOn,
        Verification verification,
        List<Evidence> evidence,
        Boolean revoked,            // NUEVO
        String revocationReason) {  // NUEVO
    ...
    public static AssertionJsonLd fromEntity(String baseUrl, Assertion entity) {
        ...
        return new AssertionJsonLd(..., entity.isRevoked ? true : null, entity.revocationReason);
    }
}
```

### 6.2 Página HTML (`htmlPayload.html`)

Se regenera el `htmlPayload` de la assertion al revocar/des-revocar (reutilizando
`CreateAssertion.Templates.htmlPayload`), agregando una **banda de estado**:

```html
{#if assertion.isRevoked}
<div class="revoked-banner">
    <strong>Esta credencial fue revocada</strong>
    <p>{assertion.revocationReason}</p>
</div>
{/if}
```

Con estilos `revoked-banner` (fondo rojo/gris, texto contrastante).

> El endpoint público `GET /api/v2/assertions/{uuid}` sigue sirviendo la página/JSON-LD,
> pero ahora con el estado revocado visible. Opcionalmente puede devolver `410 Gone`
> para metadatos de assertion revocada; se decide mantener `200` con estado explícito para
> transparencia de verificación.

---

## 7. Diseño de Implementación

Siguiendo la arquitectura de `AGENTS.md` (Resource → application → model):

### Módulos

```
src/main/java/com/gdgguadalajara/assertion/
├── application/
│   ├── AssertionMetadata.java      # regenera jsonPayload + htmlPayload (helper compartido)
│   ├── RevokeAssertion.java        # run(issuerUuid, assertionUuid, request)
│   ├── UnrevokeAssertion.java      # run(issuerUuid, assertionUuid)
│   └── RevokeBadgeAssertions.java  # run(issuerUuid, badgeClassUuid, request) — masivo
src/main/java/com/gdgguadalajara/issuer/
├── IssuerAssertionResource.java    # (modificado) PATCH revoke/unrevoke
└── IssuerBadgeClassResource.java   # (modificado) PATCH /{badgeClassUuid}/revoke
```

### Regeneración de metadatos

Se extrae la regeneración de `jsonPayload`/`htmlPayload` a un caso de uso compartido
**`AssertionMetadata.regenerate(Assertion)`** (inyecta `ObjectMapper` y el `domain`), usado por
`RevokeAssertion`, `UnrevokeAssertion` y `RevokeBadgeAssertions` sin duplicar lógica.

### Casos de uso

**`RevokeAssertion.run(UUID issuerUuid, UUID assertionUuid, RevokeAssertionRequest request)`**
- `@ApplicationScoped` + `@Transactional`
- Valida `request.reason()` no vacío → si no, `DomainException.badRequest("El motivo de revocación es obligatorio")`.
- `Assertion.findById(assertionUuid)` → si `null` → `DomainException.notFound(...)`.
- Verifica pertenencia al issuer (ver §7 Validación de pertenencia).
- Si `assertion.isRevoked` → `DomainException.badRequest("La credencial ya se encuentra revocada")`.
- Establece `isRevoked = true`, `revocationReason = reason`, regenera `jsonPayload`
  (`AssertionJsonLd.fromEntity`) y `htmlPayload` (template Qute).
- `persist()`.
- Devuelve el DTO con `revokedAt = Instant.now()`.

**`UnrevokeAssertion.run(UUID issuerUuid, UUID assertionUuid)`**
- `@Transactional`
- `Assertion.findById(assertionUuid)` → si `null` o no pertenece al issuer → `DomainException.notFound(...)`.
- Si `!assertion.isRevoked` → `DomainException.badRequest("La credencial no está revocada")`.
- Establece `isRevoked = false`, `revocationReason = null`, regenera `jsonPayload` y `htmlPayload`.
- `persist()`.

**`RevokeBadgeAssertions.run(UUID issuerUuid, UUID badgeClassUuid, RevokeBadgeAssertionsRequest request)`**
- `@ApplicationScoped` + `@Transactional`
- `BadgeClass.findById(badgeClassUuid)` → si `null` → `DomainException.notFound(...)`; verifica
  que `badgeClass.issuer.id == issuerUuid`.
- Resuelve las assertions objetivo:
  - `request.assertionIds()` vacío/null → todas las assertions **no revocadas** del badge:
    `Assertion.find("badgeClass.id = ?1 and isRevoked = false", badgeClassUuid)`.
  - Con IDs → filtra `Assertion.find("id in ?1", ids)` y conserva solo las que pertenecen
    al `badgeClassUuid` y aún no están revocadas.
- Si no hay ninguna elegible → `DomainException.badRequest("No hay credenciales elegibles para revocar")`.
- Aplica `isRevoked = true`, `revocationReason = reason` y regenera `jsonPayload`/`htmlPayload`
  por cada assertion (reutilizando la lógica de regeneración de `RevokeAssertion`).
- `persist()`; devuelve `RevokeBadgeAssertionsResponse` con `total`, `revoked`, `skipped` y `assertionIds`.

> **Regeneración compartida:** extraer la regeneración de `jsonPayload`/`htmlPayload` a un
> método común (p.ej. en `AssertionMetadata` helper) para no duplicar entre casos de uso.

### Validación de pertenencia a la organización

La anotación `@OrgRole` + `OrgSecurityFilter` ya valida que el usuario tenga membresía en el
`issuerUuid`. Además, el caso de uso debe **verificar que la assertion pertenezca al issuer**:

```java
if (assertion.badgeClass.issuer.id != issuerUuid)  // o !equals
    throw DomainException.notFound("Acreditación no encontrada");
```

> Nota: el `OrgSecurityFilter` solo valida membresía, no que la assertion pertenezca al
> `issuerUuid` de la ruta. La validación de pertenencia es responsabilidad del caso de uso.

### Resource (agregado a `IssuerAssertionResource`)

```java
@PATCH
@Path("/{assertionUuid}/revoke")
@Authenticated
@OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
public RevokeAssertionResponse revoke(UUID issuerUuid, UUID badgeClassUuid, UUID assertionUuid,
        @Valid RevokeAssertionRequest request) {
    return revokeAssertion.run(issuerUuid, assertionUuid, request);
}

@PATCH
@Path("/{assertionUuid}/unrevoke")
@Authenticated
@OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
public UnrevokeAssertionResponse unrevoke(UUID issuerUuid, UUID badgeClassUuid, UUID assertionUuid) {
    return unrevokeAssertion.run(issuerUuid, assertionUuid);
}
```

El endpoint masivo se registra en `IssuerBadgeClassResource` (que ya gestiona
`/badges/{badgeClassUuid}`):

```java
@PATCH
@Path("/{badgeClassUuid}/revoke")
@Authenticated
@OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
public RevokeBadgeAssertionsResponse revokeBadge(UUID issuerUuid, UUID badgeClassUuid,
        RevokeBadgeAssertionsRequest request) {
    return revokeBadgeAssertions.run(issuerUuid, badgeClassUuid, request);
}
```

### DTOs

```java
// model/dto/RevokeAssertionRequest.java
public record RevokeAssertionRequest(
        @NotBlank(message = "El motivo de revocación es obligatorio") String reason) {
}

// model/dto/RevokeAssertionResponse.java
public record RevokeAssertionResponse(
        UUID assertionId,
        boolean isRevoked,
        String revocationReason,
        Instant revokedAt) {
}

// model/dto/UnrevokeAssertionResponse.java
public record UnrevokeAssertionResponse(
        UUID assertionId,
        boolean isRevoked) {
}

// model/dto/RevokeBadgeAssertionsRequest.java
public record RevokeBadgeAssertionsRequest(
        @NotBlank(message = "El motivo de revocación es obligatorio") String reason,
        List<UUID> assertionIds) {   // null/vacío → todas las no revocadas
}

// model/dto/RevokeBadgeAssertionsResponse.java
public record RevokeBadgeAssertionsResponse(
        UUID issuerId,
        UUID badgeClassId,
        String reason,
        int total,
        int revoked,
        int skipped,
        Instant revokedAt,
        List<UUID> assertionIds) {
}
```

> **Aclaración de firma:** el `run()` de `RevokeAssertion` recibe `(UUID issuerUuid, UUID assertionUuid,
> RevokeAssertionRequest)` para validar pertenencia dentro del caso de uso (sobrecarga del
> método `run()` permitida por la convención).

---

## 8. UI / Frontend (Nuxt)

### 8.1 Vista de detalle de recipientes

En la sección "Emisiones" de la organización (ver **SPEC — Visibilidad de Emisiones**), cada
fila de recipiente con estado `CLAIMED` o `PENDING` incluye una acción **"Revocar"** que abre
un diálogo:

- **Motivo** (textarea obligatorio).
- Confirmación con texto de advertencia:
  *"Esta acción marcará la credencial como revocada y será visible públicamente."*

Las filas ya revocadas (`REVOKED`) muestran la acción **"Restaurar"** (con confirmación).

### 8.2 Revocación masiva por credencial

En la cabecera del detalle de una credencial (dentro de "Emisiones"), se agrega un botón
**"Revocar credencial"** dirigido a `PATCH .../badges/{badgeClassUuid}/revoke`:

- Diálogo con **motivo obligatorio** y selector de alcance:
  - **Todas las emisiones** (checkbox "Incluir todas las no revocadas").
  - **Solo seleccionadas** (las filas con checkbox marcadas en la tabla de recipientes).
- Texto de advertencia: *"Todas las credenciales seleccionadas se marcarán como revocadas y
  el estado será visible públicamente. Esta acción puede revertirse individualmente."*
- Respuesta confirmando `revoked`/`skipped` (mostrar conteo si hubo omitidas).

### 8.3 Estados visuales

- `CLAIMED` → badge teal con palomita.
- `PENDING` → badge dorado "Pendiente".
- `REVOKED` → badge rojo "Revocada" + tooltip con el motivo.

### 8.4 Servicios Orval

Agregar los endpoints `PATCH .../revoke`, `PATCH .../unrevoke` y
`PATCH .../badges/{badgeClassUuid}/revoke` al OpenAPI y regenerar con `npm run build` (orval).

---

## 9. Consideraciones de Seguridad y Privacidad

1. **Acceso por rol:** solo `OWNER`/`ADMIN` de la organización emisora pueden revocar.
2. **Pertenencia:** validación explícita de que la assertion pertenezca al `issuerUuid`
   (y al `badgeClassUuid` en el caso masivo).
3. **Privacidad:** la revocación no revela emails en texto plano de recipientes no reclamados
   (aplica la misma regla de exposición que el SPEC de emisiones).
4. **No borrado físico:** la revocación es un estado, no una eliminación. La evidencia
   permanece para auditoría.
5. **JSON-LD:** `revoked`/`revocationReason` en metadatos permiten verificación externa honesta.
6. **Transaccionalidad:** el lote masivo es atómico; no se aplica a medias.

---

## 10. Criterios de Aceptación

- [ ] Un `OWNER` y un `ADMIN` pueden revocar una assertion de su organización.
- [ ] `reason` es obligatorio; vacío devuelve `400`.
- [ ] Revocar una assertion ya revocada devuelve `409`/`400` (sin estado corrupto).
- [ ] Un `ADMIN` de otra organización no puede revocar (`403` por `OrgSecurityFilter`).
- [ ] Una assertion de otra organización no puede revocarse vía el `issuerUuid` (validación de pertenencia → `404`).
- [ ] El JSON-LD público refleja `revoked: true` y `revocationReason`.
- [ ] El HTML público muestra la banda de estado revocado.
- [ ] `unrevoke` restaura el estado y regenera JSON-LD/HTML sin el estado revocado.
- [ ] **Masivo sin IDs:** revoca todas las assertions no revocadas del badge y reporta `revoked`/`skipped`.
- [ ] **Masivo con IDs:** revoca solo los indicados, ignora IDs inexistentes o de otro badge (cuenta en `skipped`).
- [ ] **Masivo:** con ninguna elegible devuelve `400`.
- [ ] La UI muestra la acción Revocar/Restaurar y la revocación masiva según estado y rol.
- [ ] `npm run build` (orval) regenera los servicios sin errores.

---

## 11. Fuera de Alcance (futuras iteraciones)

- Notificación por email al recipiente al revocar.
- Historial/auditoría de revocaciones (fecha exacta persistida, quién revocó).
- Regeneración automática de la imagen "horneada" con estado revocado.
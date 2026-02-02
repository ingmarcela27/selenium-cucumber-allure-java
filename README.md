# 🧱 Building Block CI/CD: Publicación de Reportes (GitHub Pages) + Notificación (Google Chat)

Este repositorio contiene un ejemplo funcional de pipeline en **GitHub Actions** para:
- Ejecutar pruebas (Selenium + Cucumber)
- Generar reporte HTML con **Allure**
- Publicarlo automáticamente en **GitHub Pages** (rama `gh-pages`)
- Enviar notificación con métricas a **Google Chat Space** usando Webhook

✅ **Lo más importante:** aunque este repo usa Java/Selenium/Gradle, la idea es reutilizable.  
Si tu equipo **ya tiene su job de tests**, lo que más te sirve de aquí es el **JOB 3: PUBLISH** (publicación + notificación).

---

## 🎯 Qué problema resuelve

En equipos ágiles, los reportes suelen quedar “perdidos” en artifacts o logs.  
Este building block estandariza:

- **Visibilidad:** reporte disponible como sitio web (Pages)
- **Trazabilidad:** histórico de Allure (history) entre ejecuciones
- **Comunicación:** mensaje automático al Google Chat Space con link + métricas

---

## 🧩 Contrato de integración (lo mínimo que tu pipeline debe entregar)

Este building block necesita **un reporte HTML estático** para publicar.

Tienes 2 opciones:

### ✅ Opción A (recomendada): ya tienes el HTML
Tu pipeline ya genera HTML (Allure, Playwright, Cypress, etc.).  
➡️ Solo conectas el **job de publish** para publicar + notificar.

### 🧪 Opción B: tienes resultados crudos
Tu pipeline genera resultados (ej: `allure-results`) y luego un job genera el HTML.  
➡️ Puedes reutilizar el **job report** de este repo o adaptar el tuyo.

> Regla universal: **si no hay HTML generado → no hay publicación en Pages.**

---

## 🧱 Arquitectura del pipeline (alto nivel)

```text
Push / PR / Manual
        ↓
     TEST JOB
        ↓
    REPORT JOB
        ↓
   PUBLISH JOB  (solo main + manual/schedule)
      ↙     ↘
GitHub Pages  Google Chat
```
---

## 📁 Archivo clave en este repo

- **Workflow:** `.github/workflows/ci.yml`

Este workflow está dividido en **3 jobs**:

1. **test:** 
- Ejecuta pruebas
- Sube `allure-results` como *artifact*

2. **report:** 
- Descarga resultados
- (Opcional) restaura *history* desde gh-pages
- Genera HTML (ejemplo: ./gradlew allureReport)
- Extrae métricas y las expone como outputs: passed, failed, broken, total.

3. **publish:** 
- Descarga el artifact del reporte HTML
- Valida que exista *index.html* 
- Notifica a Google Chat con métricas y link
- Publica a Pages (rama *gh-pages* / carpeta *allureReport*)

---

## 🚦 Cuándo se ejecuta (y cuándo publica)

### ✅ El workflow se dispara con:
- **Push** a `main`
- **Pull Request**
- **Manual** (`workflow_dispatch`)
- **Schedule diario**
    - `0 13 * * *` → **13:00 UTC = 8:00 AM Colombia (UTC-5)**

### ✅ Importante: la publicación a Pages NO ocurre en cada push/PR
El job **`publish`** está diseñado para correr **solo** cuando:

- **Rama:** `main`
- **Evento:** `schedule` **o** `workflow_dispatch`

Esto evita “spam” de publicaciones por cada cambio.  
En **push/PR** se generan **artifacts** y **métricas**, pero **no se publica**.

---

## ✅ Requisitos del repositorio (muy importantes)

### 1) Permisos del workflow para crear/actualizar `gh-pages`

En el YAML ya está:

```yaml
permissions:
  contents: write
```

## ✅ Permisos de escritura para GitHub Actions (obligatorio)

Además del workflow, en el repo debes habilitar permisos de escritura para Actions:

**Ruta:** Repo → **Settings** → **Actions** → **General** → **Workflow permissions**

- ✅ **Read and write permissions**
- (Opcional) **Allow GitHub Actions to create and approve pull requests**

> Sin esto, el workflow no puede hacer push a `gh-pages`.

---

## 🌐 Configurar GitHub Pages (para publicar el reporte)

1. Repo → **Settings**
2. Menú → **Pages**
3. En **Build and deployment**:
    - **Source:** Deploy from a branch
    - **Branch:** `gh-pages`
    - **Folder:** `/ (root)`
4. **Save**

⚠️ **Nota:** la rama `gh-pages` aparecerá solo después de la primera publicación exitosa (manual o schedule en main).

---

## 🔐 Secrets requeridos

### 1) `GITHUB_TOKEN`
- No se crea. GitHub lo inyecta automáticamente.
- Solo asegúrate de tener:
    - `permissions: contents: write` en el workflow
    - **Read and write permissions** activado en el repo

---

### 2) `GCHAT_WEBHOOK_URL` (Google Chat)
Se usa para enviar la notificación al Space.

**Crear webhook**
1. Google Chat → entrar al **Space**
2. Opciones del Space → **Aplicaciones e integraciones**
3. **Añadir webhooks**
4. Copiar la URL

**Guardar en GitHub**
1. Repo → **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret**
3. **Name:** `GCHAT_WEBHOOK_URL`
4. **Value:** (pegar URL)

- ✅ Si no configuras este secret, el workflow no falla; solo salta la notificación.
---

## 🛠️ Variables importantes (para adaptar rápido)

En este repo:

```yaml
env:
  RESULTS_DIR: build/allure-results
  REPORT_DIR: build/reports/allure-report/allureReport
```
**Qué significa:**

- `RESULTS_DIR`: carpeta donde quedan los resultados crudos (Allure)
- `REPORT_DIR`: carpeta donde queda el HTML final listo para publicar

---


## 🧪 Qué hace especial este workflow

### ✅ 1) Restaura Allure history automáticamente (tendencias)
En el job **`report`**:

- Hace `git fetch` de `gh-pages`
- Monta un `worktree`
- Si existe `allureReport/history`, lo copia a `allure-results/history`

✅ Resultado: Allure puede generar **tendencias** entre ejecuciones.

---

### ✅ 2) Extrae métricas reales del reporte
Lee el archivo:

- `${REPORT_DIR}/widgets/summary.json`

Y expone como **outputs**:

- `passed`
- `failed`
- `broken`
- `total`

Luego el job **`publish`** usa esas métricas para la notificación.

---

### ✅ 3) Publica solo cuando debe (main + manual/schedule)
Evita:

- Publicar por cada `push`
- Publicar en `pull_request`

Pero igual deja evidencia (**artifacts**) y métricas para debugging.

---

### ✅ 4) Cache-busting del link del reporte
El link que llega en Google Chat incluye:

- `?v=${GITHUB_RUN_ID}`

Esto ayuda a evitar que el navegador muestre un reporte “viejo” por caché.


---

## ▶️ Cómo ejecutar (recomendado)

### Opción 1: Ejecutarlo manualmente (para probar Pages ya)

1. GitHub → **Actions**
2. Selecciona el **workflow**
3. Haz clic en **Run workflow**

✅ Esto sí dispara **publish** (si estás en `main`).

---

### Opción 2: Esperar el schedule

Se ejecuta automáticamente **todos los días** a:

- **8:00 AM Colombia** (**13:00 UTC**)


----

## 📌 Dónde ver el reporte publicado

Cuando el job **publish** corre en `main`, el reporte queda en:

`https://<owner>.github.io/<repo>/allureReport/`

> `allureReport` viene de `destination_dir: allureReport`.
> 
---

## 🔁 Cómo reutilizar este building block en tu proyecto

### ✅ Opción 1: Copiar solo el job `publish` (si ya generas HTML)

1. Copia el job `publish` a tu workflow.
2. Asegúrate de tener el HTML en tu `REPORT_DIR` (debe existir `index.html`).
3. Ajusta:
    - `REPORT_DIR`
    - `destination_dir` (opcional)
4. Configura:
    - GitHub Pages
    - Permisos del repo (Actions)
    - Secret `GCHAT_WEBHOOK_URL`

---

### 🧪 Opción 2: Copiar `report` + `publish` (si tienes `allure-results`)

Si ya produces `allure-results`, copia `report` + `publish` y asegúrate de:

- `RESULTS_DIR` apunta a donde guardas los resultados
- Existe un comando equivalente a `./gradlew allureReport`

---

## ✅ Checklist de validación (cuando lo implementes en tu repo)

- [ ] El workflow tiene `permissions: contents: write`
- [ ] Repo **Settings → Actions → General** → **Read and write permissions** ✅
- [ ] En `main` se ejecuta el job `publish` (no skipped)
- [ ] Existe `REPORT_DIR` con HTML (si no, no publica)
- [ ] Rama `gh-pages` aparece en **Code → Branches**
- [ ] **Settings → Pages** apunta a `gh-pages`
- [ ] (Opcional) `GCHAT_WEBHOOK_URL` creado y guardado como secret
- [ ] El link del reporte abre correctamente en el browser

---

## 🧯 Troubleshooting rápido

### “❌ No index.html found…”
**Posibles causas:**
- Tu `REPORT_DIR` no está apuntando al HTML real
- El reporte no se generó
- Cambió la ruta en tu framework

**Solución:**
- Revisa el output real del build y ajusta `REPORT_DIR`

---

### No aparece `gh-pages`
**Posibles causas:**
- No has ejecutado **manual** o **schedule** en `main` (push/PR **NO** publica)
- Faltan permisos de escritura en Actions

**Solución:**
- Corre el workflow manualmente desde **Actions** (en `main`)
- Habilita **Read and write permissions**

---

### No llega mensaje a Google Chat
**Posibles causas:**
- `GCHAT_WEBHOOK_URL` no existe o está mal
- El workflow lo omite de forma segura

**Solución:**
- Revisa el secret y prueba el webhook

---
## 📌 Sugerencia de uso corporativo (para equipos)

Este repo puede servir como:

- **Referencia técnica:** copy/paste del job `publish`
- **Plantilla base:** estandarizar publicación y comunicación de resultados
- **Base para escalar:** convertirlo en un workflow reusable (`workflow_call`) para que los equipos solo lo “llamen”


---

## 📁 Repo de referencia

- **Repositorio:** `ingmarcela27/ci-reporting-building-block`
- **Workflow:** `.github/workflows/ci.yml`
- **GitHub Pages (reporte):** `https://ingmarcela27.github.io/ci-reporting-building-block/allureReport/`
> **Tip:** Si tu pipeline ya genera un HTML, copia solo el job **PUBLISH** y ajusta `REPORT_DIR`.


---




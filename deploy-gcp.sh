#!/bin/bash
# =============================================================
#  Products API — Deploy to Cloud Run + Cloud SQL (PostgreSQL)
# =============================================================
# Uso:
#   1. Configura PROJECT_ID con tu proyecto de GCP
#   2. Ajusta DB_PASSWORD por una contraseña segura
#   3. Ejecuta: bash deploy-gcp.sh
# =============================================================

set -euo pipefail

PROJECT_ID="project-3c88d352-0eed-4a0d-9d5"
DB_PASSWORD="cambiar-por-password"   # ← CAMBIAR
REGION="us-central1"

echo "=== 1. Configurar proyecto ==="
gcloud config set project "$PROJECT_ID"

echo "=== 2. Habilitar APIs necesarias ==="
gcloud services enable \
  cloudrun.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  secretmanager.googleapis.com

echo "=== 3. Crear instancia Cloud SQL (PostgreSQL) ==="
gcloud sql instances create products-db \
  --database-version=POSTGRES_18 \
  --tier=db-f1-micro \
  --region="$REGION"

echo "=== 4. Crear base de datos y usuario ==="
gcloud sql databases create products --instance=products-db
gcloud sql users create liteThinking \
  --instance=products-db \
  --password="$DB_PASSWORD"

INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe products-db \
  --format="value(connectionName)")
echo "Connection name: ${INSTANCE_CONNECTION_NAME}"

echo "=== 5. Crear secretos en Secret Manager ==="
echo -n "$DB_PASSWORD" | gcloud secrets create DB_PASSWORD --data-file=-
echo -n "clave-secreta-temporal-cambiar-en-produccion-de-al-menos-256-bits" | \
  gcloud secrets create JWT_SECRET --data-file=-
echo -n "0.00026" | gcloud secrets create TASA_COP_USD --data-file=-
echo -n "0.00024" | gcloud secrets create TASA_COP_EUR --data-file=-

echo "=== 6. Crear repositorio Artifact Registry ==="
gcloud artifacts repositories create products \
  --repository-format=docker \
  --location="$REGION"

gcloud auth configure-docker "$REGION-docker.pkg.dev"

echo "=== 7. Completado ==="
echo ""
echo "Resumen de recursos creados:"
echo "  - Cloud SQL:     ${INSTANCE_CONNECTION_NAME}"
echo "  - Artifact Repo: ${REGION}-docker.pkg.dev/${PROJECT_ID}/products"
echo "  - Secrets:       DB_PASSWORD, JWT_SECRET, TASA_COP_USD, TASA_COP_EUR"
echo ""
echo "Próximo paso — construir y desplegar:"
echo "  gcloud builds submit"
echo ""
echo "O bien, si configuras un trigger en Cloud Build:"
echo "  https://console.cloud.google.com/cloud-build/triggers"

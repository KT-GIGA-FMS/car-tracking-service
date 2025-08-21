#!/bin/bash

# Azure Container Apps 배포 스크립트
# 사용법: ./deploy-azure.sh <resource-group> <container-app-name> <image-name>

set -e

RESOURCE_GROUP=${1:-"car-service-rg"}
CONTAINER_APP_NAME=${2:-"car-service-app"}
IMAGE_NAME=${3:-"car-service:latest"}
LOCATION=${4:-"koreacentral"}

echo "🚀 Azure Container Apps 배포 시작..."
echo "Resource Group: $RESOURCE_GROUP"
echo "Container App: $CONTAINER_APP_NAME"
echo "Image: $IMAGE_NAME"
echo "Location: $LOCATION"

# Docker 이미지 빌드
echo "📦 Docker 이미지 빌드 중..."
docker build -t $IMAGE_NAME .

# Azure Container Registry에 푸시 (선택사항)
# az acr build --registry <your-registry> --image $IMAGE_NAME .

# Resource Group 생성 (없는 경우)
echo "🏗️ Resource Group 생성 중..."
az group create --name $RESOURCE_GROUP --location $LOCATION

# Container Apps Environment 생성
echo "🌍 Container Apps Environment 생성 중..."
az containerapp env create \
  --name "${CONTAINER_APP_NAME}-env" \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION

# Container App 생성
echo "📱 Container App 생성 중..."
az containerapp create \
  --name $CONTAINER_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --environment "${CONTAINER_APP_NAME}-env" \
  --image $IMAGE_NAME \
  --target-port 8080 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 3 \
  --cpu 0.5 \
  --memory 1Gi \
  --env-vars \
    SPRING_DATASOURCE_URL="jdbc:postgresql://your-postgres-server:5432/car" \
    SPRING_DATASOURCE_USERNAME="your-username" \
    SPRING_DATASOURCE_PASSWORD="your-password"

echo "✅ 배포 완료!"
echo "🌐 Container App URL: https://$(az containerapp show --name $CONTAINER_APP_NAME --resource-group $RESOURCE_GROUP --query properties.configuration.ingress.fqdn -o tsv)"

echo ""
echo "📋 다음 단계:"
echo "1. 환경 변수 설정 (데이터베이스 연결 정보)"
echo "2. 헬스체크 확인"
echo "3. API 테스트"

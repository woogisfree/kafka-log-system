#!/bin/bash

SERVICE=$1

if [ -z "$SERVICE" ]; then
  echo "❌ Usage: ./deploy.sh [producer-api|consumer-worker]"
  exit 1
fi

echo "Step 1: Building JAR for $SERVICE..."

cd "$SERVICE" || { echo "❌ Directory $SERVICE not found"; exit 1; }
./gradlew clean bootJar || { echo "❌ Gradle build failed"; exit 1; }
cd ../infra || { echo "❌ infra directory not found"; exit 1; }

echo "Step 2: Building Docker image for $SERVICE..."
docker compose build "$SERVICE" || { echo "❌ Docker build failed"; exit 1; }

echo "Step 3: Restarting $SERVICE container..."
docker compose up -d "$SERVICE" || { echo "❌ Docker run failed"; exit 1; }

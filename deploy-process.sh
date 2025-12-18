#!/bin/bash

# Helper script to deploy BPMN resources to Camunda 8 (Local or SaaS)
# Usage: ./deploy-process.sh src/main/resources/fetch_picture.bpmn

RESOURCE=$1

if [ -z "$RESOURCE" ]; then
  echo "Usage: ./deploy-process.sh <path-to-bpmn-file>"
  exit 1
fi

# Load variables from .env if it exists
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
elif [ -f saas.env ]; then
  export $(grep -v '^#' saas.env | xargs)
fi

# Check if we are using SaaS (Cluster ID is set)
if [ ! -z "$ZEEBE_CLIENT_CLOUD_CLUSTER_ID" ]; then
  echo "Deploying to Camunda SaaS Cluster: $ZEEBE_CLIENT_CLOUD_CLUSTER_ID"
  
  # Standard zbctl environment variables
  export ZEEBE_ADDRESS="${ZEEBE_CLIENT_CLOUD_CLUSTER_ID}.${ZEEBE_CLIENT_CLOUD_REGION:-bru-2}.zeebe.camunda.io:443"
  export ZEEBE_CLIENT_ID="$ZEEBE_CLIENT_CLOUD_CLIENT_ID"
  export ZEEBE_CLIENT_SECRET="$ZEEBE_CLIENT_CLOUD_CLIENT_SECRET"
  export ZEEBE_AUTHORIZATION_SERVER_URL="https://login.cloud.camunda.io/oauth/token"
  
  zbctl deploy resource "$RESOURCE"
else
  echo "Deploying to Local Zeebe (localhost:26500)"
  zbctl --insecure --address localhost:26500 deploy resource "$RESOURCE"
fi

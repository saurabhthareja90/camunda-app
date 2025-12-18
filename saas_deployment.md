# SaaS Deployment Guide

This guide covers how to deploy and troubleshoot the Camunda Client Application when connecting to a Camunda 8 SaaS (Cloud) instance.

## 🔑 Configuration

The application uses environment variables for configuration. You can use a `.env` or `saas.env` file.

### Required Variables
- `ZEEBE_CLIENT_CLOUD_CLUSTER_ID`: Your Cluster ID from Camunda Console.
- `ZEEBE_CLIENT_CLOUD_CLIENT_ID`: Your Client ID.
- `ZEEBE_CLIENT_CLOUD_CLIENT_SECRET`: Your Client Secret.
- `ZEEBE_CLIENT_CLOUD_REGION`: (Optional) Defaults to `bru-2`.
- `ZEEBE_CLIENT_SECURITY_PLAINTEXT`: Must be `false` for SaaS.

## 🚀 Deploying BPMN using CLI (`zbctl`)

You can use `zbctl` to deploy your processes from the terminal.

### 1. Using the Helper Script (Recommended)
```bash
# This automatically uses your .env or saas.env variables
./deploy-process.sh src/main/resources/fetch_picture.bpmn
```

### 2. Using `zbctl` Directly
```bash
export ZEEBE_ADDRESS='[Cluster ID].[Region].zeebe.camunda.io:443'
export ZEEBE_CLIENT_ID='[Client ID]'
export ZEEBE_CLIENT_SECRET='[Client Secret]'
export ZEEBE_AUTHORIZATION_SERVER_URL='https://login.cloud.camunda.io/oauth/token'

zbctl deploy resource src/main/resources/fetch_picture.bpmn
```

# Camunda 8 Client App - Animal Picture Fetcher

A Spring Boot application that orchestrates fetching animal pictures using Camunda 8 (Zeebe).

![Architecture Diagram](architecture.md)

## Features
- **BPMN Process**: Self-deploying flow (Start -> Fetch Picture -> End).
- **REST API**: Starts a process instance and awaits the result.
- **Job Worker**: Subscribes to `fetch-picture` tasks and resolves image URLs.
- **Web UI**: Simple frontend to select an animal (Cat/Dog/Bear) and view the result.

## Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- Camunda 8 Cluster (SaaS or Self-Managed)

---

## 🚀 Local Deployment (Self-Managed)

These options run a Zeebe broker locally on your machine.

### Option 1: Docker Compose (Recomendended)
This starts the app, a local Zeebe broker, and Redis in one command.

```bash
docker-compose up --build
```
Open [http://localhost:8080](http://localhost:8080).

### Option 2: Kubernetes (Helm)
The provided Helm chart includes a standalone Zeebe broker and a Redis instance for internal development.

**1. Build the Docker image**:
```bash
# Docker now handles the build internally
docker build -t camunda-client-app-app:latest .
```

**2. Install using the chart**:
```bash
# Deploys the app, an internal Zeebe broker, and Redis
helm install my-release ./helm
```
**Default Access**: [http://localhost:8080](http://localhost:8080) (via LoadBalancer).

### Option 3: Manual Run
Ensure you have a Zeebe broker running on `localhost:26500`, then:
```bash
mvn spring-boot:run
```

---

## ☁️ SaaS Deployment (Camunda 8 Cloud)

Use this mode to connect your application to a cluster managed by Camunda.

### 1. Configuration (`saas.env`)
Fill in your cluster credentials in the provided `saas.env` file:
```env
ZEEBE_CLIENT_CLOUD_CLUSTER_ID=your-id
ZEEBE_CLIENT_CLOUD_CLIENT_ID=your-client-id
ZEEBE_CLIENT_CLOUD_CLIENT_SECRET=your-secret
ZEEBE_CLIENT_CLOUD_REGION=sin-2
ZEEBE_CLIENT_SECURITY_PLAINTEXT=false
```

### 2. Running with Docker

```bash
docker build -t camunda-client-app-app:latest .
docker run -p 8080:8080 --env-file saas.env camunda-client-app-app
```

### 3. Running with Helm
This disables the internal Zeebe broker and connects to the cloud:
```bash
helm install my-release ./helm -f ./helm/values-saas.yaml
```

### 4. Running Manually
```bash
# Export variables from saas.env
export $(grep -v '^#' saas.env | xargs)

# Start the app
java -jar target/camunda-client-app-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Testing

The project includes automated tests for all major components.

### Run all tests:
```bash
mvn test
```

### What is tested:
- **`CamundaClientApplicationTests`**: Verifies the Spring Boot application context loads correctly.
- **`ProcessControllerTest`**: Mocks Zeebe to verify the REST API initiates processes with the correct variables.
- **`PictureWorkerTest`**: Verifies the image URL selection logic and Zeebe job completion.

---

## Useful Commands

| Command | Description |
|---------|-------------|
| `kubectl get pods` | View running pods in Kubernetes. |
| `kubectl logs -f <pod-name>` | Follow application or worker logs. |
| `helm uninstall my-release` | Remove the app and its resources. |

---

## ☁️ Connecting to Your Own Cluster (SaaS)

You can point this application to any external Camunda cluster by providing your credentials in an environment file.

1.  **Copy the template**:
    ```bash
    cp camunda-config.env.example .env
    ```
2.  **Edit `.env`**: Fill in your `Cluster ID`, `Client ID`, and `Client Secret` from the Camunda Console.
3.  **Run with configuration**:
    - **Docker Compose**: The app service will automatically pick up a file named `.env` if present.
    - **Docker Manual**: 
      ```bash
      docker run -p 8080:8080 --env-file .env camunda-client-app-app
      ```
    - **Helm**: 
      ```bash
      helm install my-release ./helm --set-file env.zeebeData=.env
      ```

---

## 🚀 Deploying Processes via CLI (`zbctl`)

If you want to deploy the BPMN process manually or update it, you can use the provided helper script.

1.  **Using the helper script** (recommended):
    ```bash
    # This automatically uses your .env or saas.env
    ./deploy-process.sh src/main/resources/fetch_picture.bpmn
    ```

2.  **Using `zbctl` directly** (SaaS):
    ```bash
    export ZEEBE_ADDRESS='[Cluster ID].[Region].zeebe.camunda.io:443'
    export ZEEBE_CLIENT_ID='[Client ID]'
    export ZEEBE_CLIENT_SECRET='[Client Secret]'
    export ZEEBE_AUTHORIZATION_SERVER_URL='https://login.cloud.camunda.io/oauth/token'

    zbctl deploy resource src/main/resources/fetch_picture.bpmn
    ```

3.  **Using `zbctl` directly** (Local):
    ```bash
    zbctl --insecure --address localhost:26500 deploy resource src/main/resources/fetch_picture.bpmn
    ```
| [Architecture Diagram](architecture.md) | View the system design and process flow. |

## 📦 Picture Storage (Redis)

Fetched picture URLs are automatically cached/stored in a Redis instance.
- **Local/Docker Run**: Redis starts automatically via `docker-compose`.
- **SaaS Run**: The app will attempt to connect to `localhost:6379` by default unless `SPRING_DATA_REDIS_HOST` is provided.

### Inspecting Stored Pictures:
```bash
docker exec -it camunda-client-app-redis-1 redis-cli KEYS "*"
# To view a specific record:
docker exec -it camunda-client-app-redis-1 redis-cli GET <key-name>
```

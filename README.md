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
This starts the app and a local Zeebe broker in one command.
```bash
docker-compose up --build
```
Open [http://localhost:8080](http://localhost:8080).

### Option 2: Kubernetes (Helm)
The provided Helm chart includes a standalone Zeebe broker for internal development.

**Note**: You must build the Docker image locally first so Kubernetes can find it:
```bash
docker build -t camunda-client-app-app:latest .
```

Then install using the chart:
```bash
# Deploys both the app and an internal Zeebe broker
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
| [Architecture Diagram](architecture.md) | View the system design and process flow. |

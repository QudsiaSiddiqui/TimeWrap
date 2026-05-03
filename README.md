# TimeWarp — Event-Sourced Time Travel Engine

TimeWarp is a production-grade backend system built using Spring Boot and PostgreSQL that enables time travel, undo/redo, branching, and state reconstruction using an event-sourced architecture.

It also includes a React-based visual timeline UI with graph-based interaction, diff viewing, and merge capabilities, inspired by Git-like systems.

---

## Features

### Core Architecture
- Event-driven system (no direct state persistence)
- Full event sourcing model
- Deterministic state reconstruction via replay

---

### Time Travel & State Reconstruction
- Reconstruct state at any:
  - Timestamp
  - Event ID
  - Version (nth event)
- Stateless reads — state is always rebuilt from events

---

### Undo / Redo System
- Stack-based undo/redo
- Reverse events generated using `beforeState`
- Redo uses `afterState`
- System events do not clear redo stack

---

### Branching & Timeline Control
- Checkout any past event (time travel)
- Create alternate timelines (branching)
- Persistent HEAD tracking (database-backed)
- Git-like timeline behavior

---

### Merge Engine
- Merge two timeline states
- Supported strategies:
  - Source Wins
  - Target Wins
  - Manual Merge
- Conflict detection and reporting

---

### Diff Engine
- Compare any two events
- Field-level difference detection
- Side-by-side UI visualization (React)

---

### Snapshot Optimization
- Periodic snapshot creation
- Reduces replay cost for long timelines
- Hybrid reconstruction (snapshot + events)

---

### Visual Timeline (Frontend)
- Graph-based timeline using React Flow
- Interactive nodes:
  - View state
  - Checkout
  - Select source/target
- HEAD highlighting
- Diff viewer UI
- Merge controls

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Jackson (JSON processing)

### Frontend
- React
- React Flow (graph visualization)
- Axios (API calls)
- Framer Motion (animations)

---

## Project Structure
timewarp/
├── backend/
│ ├── controller/
│ ├── service/
│ ├── engine/
│ ├── entity/
│ ├── repository/
│ └── snapshot/
│
├── frontend/
│ ├── components/
│ │ ├── TimelineGraph.jsx
│ │ ├── DiffViewer.jsx
│ │ └── StateViewer.jsx
│ ├── services/
│ │ └── api.js
│ └── App.jsx


---

## API Endpoints

### Event APIs
POST /events
GET /events
GET /events/{entityId}


---

### State Reconstruction

GET /events/{entityId}/state?timestamp=...
GET /events/{entityId}/state?eventId=...
GET /events/{entityId}/state?version=...

---

### Time Control

POST /time/undo
POST /time/redo
POST /events/{entityId}/checkout?eventId=...
GET /events/{entityId}/head


---

### Diff & Merge

GET /events/{entityId}/diff?fromEventId=&toEventId=
POST /events/{entityId}/merge

---

## How to Run

### Backend
cd backend
./mvnw spring-boot:run

### Frontend
cd frontend
npm install
npm run dev

### Key Design Principles
Event sourcing over CRUD
Immutability of history
State as a derived concept
Deterministic replay
Separation of command vs query logic

### Use Cases
Financial audit systems
Time-travel debugging
Distributed system tracing
Versioned data systems
Simulation engines

### What Makes This Unique
Combines event sourcing with Git-like branching
Supports time travel at multiple levels
Includes a visual graph-based timeline UI
Implements diff and merge logic on state
Designed as a production-grade backend system
Future Improvements
WebSocket-based real-time updates
Multi-user conflict resolution
Role-based access control
Deployment (Docker and cloud)
Advanced graph layout for complex branching


### Author

Qudsia Siddiqui



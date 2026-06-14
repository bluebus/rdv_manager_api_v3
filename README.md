# RDV Manager API

A Spring Boot 3.4.3 + MongoDB REST API for appointment booking, with a built-in web frontend served as static files.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17 |
| Framework | Spring Boot 3.4.3 |
| Database | MongoDB |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Password hashing | BCrypt |
| DTO mapping | MapStruct 1.6.3 |
| Boilerplate reduction | Lombok 1.18.30 |
| Frontend | Vanilla HTML/CSS/JS (served as static files) |

---

## Project Structure

```
src/main/java/com/grey/rdv_manager_api/
├── controller/          REST controllers for each entity
├── domain/
│   ├── enums/           Role, ReservationStatus, ReminderMethod, Weekday
│   └── model/           MongoDB document entities
├── exception/           GlobalExceptionHandler
├── mapper/              MapStruct mappers (DTO ↔ entity)
├── payload/
│   ├── request/         Input DTOs with Bean Validation
│   └── response/        Output DTOs
├── repository/          MongoRepository interfaces
├── security/            JWT filter, token provider, UserDetailsService, SecurityConfig
└── service/             Service interfaces + implementations

src/main/resources/
├── application.yml      Server, MongoDB, Jackson, JWT config
└── static/
    ├── index.html       Client booking portal
    └── admin.html       Admin management portal
```

---

## Domain Model

| Collection | Entity | Description |
|---|---|---|
| `clients` | `Client` | User accounts — roles: ADMIN, CLIENT |
| `structures` | `Structure` | Physical locations (clinics, offices, etc.) |
| `services` | `ServiceEntity` | Services offered by a structure |
| `slots` | `Slot` | Bookable time slots linked to a service |
| `reservations` | `Reservation` | Client bookings of a slot |
| `reminders` | `Reminder` | Notification reminders for reservations |
| `service_availability` | `ServiceAvailability` | Weekly availability schedule per service |
| `audit_logs` | `AuditLog` | System audit trail for reservation actions |

---

## Roles

| Role | Access |
|---|---|
| `ADMIN` | Full access — manages structures, services, slots, clients, reservations, audit logs |
| `CLIENT` | Can view services and slots, create and view own reservations |

---

## Security

- JWT tokens issued on login, valid for 24 hours
- Tokens stored in `sessionStorage` on the frontend (cleared on tab close)
- BCrypt password hashing on registration
- `JwtAuthenticationFilter` validates every request before Spring Security rules apply
- Role-based access enforced in `SecurityConfig`

### Endpoint Access Rules

| Endpoint | Access |
|---|---|
| `POST /api/auth/register` | Public |
| `POST /api/auth/login` | Public |
| `GET /api/services/**` | Any authenticated user |
| `GET /api/slots/**` | Any authenticated user |
| `GET /api/reservations/by-client/{id}` | Any authenticated user |
| `/api/reservations/**` | Any authenticated user |
| `/api/reminders/**` | Any authenticated user |
| `/api/clients/**` | ADMIN only |
| `/api/structures/**` | ADMIN only |
| `/api/services/**` (write) | ADMIN only |
| `/api/slots/**` (write) | ADMIN only |
| `/api/audit-logs/**` | ADMIN only |
| `/api/service-availabilities/**` | ADMIN only |

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new client account |
| POST | `/api/auth/login` | Login and receive JWT token |

### Structures
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/structures` | List all structures |
| GET | `/api/structures/{id}` | Get structure by ID |
| POST | `/api/structures` | Create structure |
| PUT | `/api/structures/{id}` | Update structure |
| DELETE | `/api/structures/{id}` | Delete structure |

### Services
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/services` | List all services (includes `structureName`) |
| GET | `/api/services/{id}` | Get service by ID |
| POST | `/api/services` | Create service |
| PUT | `/api/services/{id}` | Update service |
| DELETE | `/api/services/{id}` | Delete service |

### Slots
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/slots` | List all slots |
| GET | `/api/slots/{id}` | Get slot by ID |
| POST | `/api/slots` | Create slot |
| PUT | `/api/slots/{id}` | Update slot |
| DELETE | `/api/slots/{id}` | Delete slot |

### Reservations
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reservations` | List all reservations |
| GET | `/api/reservations/{id}` | Get reservation by ID |
| GET | `/api/reservations/by-client/{clientId}` | Get reservations for a specific client |
| POST | `/api/reservations` | Create reservation (status defaults to PENDING) |
| PUT | `/api/reservations/{id}` | Update reservation status |
| DELETE | `/api/reservations/{id}` | Delete reservation |

### Clients
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/clients` | List all clients |
| GET | `/api/clients/{id}` | Get client by ID |
| POST | `/api/clients` | Create client (ADMIN) |
| PUT | `/api/clients/{id}` | Update client |
| DELETE | `/api/clients/{id}` | Delete client |

### Reminders
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reminders` | List all reminders |
| GET | `/api/reminders/{id}` | Get reminder by ID |
| POST | `/api/reminders` | Create reminder |
| PUT | `/api/reminders/{id}` | Update reminder |
| DELETE | `/api/reminders/{id}` | Delete reminder |

### Service Availability
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/services/{serviceId}/availability` | Get availability for a service |
| POST | `/api/services/{serviceId}/availability` | Create availability entry |
| GET | `/api/service-availabilities/{id}` | Get by ID |
| PUT | `/api/service-availabilities/{id}` | Update availability |
| DELETE | `/api/service-availabilities/{id}` | Delete availability |

### Audit Logs
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/audit-logs` | List all audit logs |
| GET | `/api/audit-logs/{id}` | Get by ID |
| GET | `/api/audit-logs/entity?entityName=&entityId=` | Filter by entity |

---

## Reservation Flow

```
CLIENT registers / logs in
        ↓
Browse services (GET /api/services)
  → structureName embedded in response
        ↓
Select service → browse slots (GET /api/slots, filtered by serviceId + available > 0)
        ↓
Confirm booking (POST /api/reservations)
  → status = PENDING
  → audit log: CREATE
        ↓
ADMIN reviews in admin portal
        ↓
ADMIN confirms (PUT /api/reservations/{id} status=CONFIRMED)
  → slot.available decremented by 1
  → audit log: UPDATE (PENDING → CONFIRMED)
        ↓
        OR
        ↓
ADMIN cancels (PUT /api/reservations/{id} status=CANCELLED)
  → slot.available restored if was CONFIRMED
  → audit log: UPDATE
```

### Slot Availability Rules

| Transition | Effect on `slot.available` |
|---|---|
| Reservation created (PENDING) | No change |
| PENDING → CONFIRMED | `-1` |
| CONFIRMED → CANCELLED | `+1` (capped at capacity) |
| PENDING → CANCELLED | No change |
| Reservation deleted (was CONFIRMED) | `+1` restored |
| Slot hidden from booking portal when | `available == 0` |

---

## Configuration (`application.yml`)

```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: rdv_manager_db_202606_test
  jackson:
    serialization:
      write-dates-as-timestamps: false  # ISO string dates in JSON

app:
  jwt:
    secret: <base64-encoded-secret-min-32-bytes>
    expiration-ms: 86400000  # 24 hours
```

---

## Running the Project

**Prerequisites:** Java 17, Maven, MongoDB running on `localhost:27017`

```bash
# Clone
git clone https://github.com/bluebus/rdv_manager_api_v3
cd rdv_manager_api_v3

# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

App starts at `http://localhost:8080`

---

## Seeding Initial Data

After the app is running, create an admin account via the API then promote it in MongoDB.

```bash
# 1. Register admin account
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Super", "lastName": "Admin",
    "email": "admin@rdv.com",
    "phone": "+60123456789",
    "password": "Admin@1234",
    "roles": ["ADMIN"]
  }'
```

```js
// 2. Promote to ADMIN in mongosh (register saves as CLIENT by default)
use rdv_manager_db_202606_test
db.clients.updateOne({ email: "admin@rdv.com" }, { $set: { roles: ["ADMIN"] } })
```

```bash
# 3. Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@rdv.com","password":"Admin@1234"}'

# 4. Create a structure (replace TOKEN with value from step 3)
curl -X POST http://localhost:8080/api/structures \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "name": "Klinik Cahaya",
    "description": "General health clinic",
    "address": "123 Jalan Ampang, Kuala Lumpur",
    "phone": "+60312345678",
    "email": "info@klinikcahaya.com"
  }'

# 5. Create a service (replace STRUCTURE_ID with id from step 4 response)
curl -X POST http://localhost:8080/api/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "structureId": "<STRUCTURE_ID>",
    "name": "General Consultation",
    "description": "Walk-in GP consultation"
  }'

# 6. Create a slot (replace SERVICE_ID with id from step 5 response)
curl -X POST http://localhost:8080/api/slots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "serviceId": "<SERVICE_ID>",
    "date": "2026-07-01",
    "startTime": "09:00",
    "endTime": "09:30",
    "capacity": 5
  }'
```

---

## Frontend Portals

### `http://localhost:8080/index.html` — Client Portal

- Register or login as CLIENT
- Browse available services — shows service name, description, and linked structure
- Select a service → view available time slots → confirm booking
- View own reservations with status badge, location, date, time, and booked-on timestamp

### `http://localhost:8080/admin.html` — Admin Portal

- Login as ADMIN only (CLIENT accounts are blocked at login)
- **Dashboard** — live counts for structures, services, clients, reservations + recent reservations table with inline confirm/cancel actions
- **Structures** — full CRUD (name, description, address, phone, email)
- **Services** — full CRUD, shows linked structure name
- **Slots** — full CRUD, shows service and structure, available count colour-coded
- **Clients** — full CRUD, role badges (ADMIN / CLIENT)
- **Reservations** — confirm / cancel with enriched view showing client name, email, service, structure, date, time, booked-on timestamp
- **Audit logs** — read-only chronological log of reservation CREATE / UPDATE / DELETE actions

---

## Audit Logging

Audit entries are written automatically for reservation lifecycle events:

| Action | Trigger | Performed by |
|---|---|---|
| `CREATE` | Client makes a booking | Client UUID |
| `UPDATE` | Admin confirms or cancels | `ADMIN` |
| `DELETE` | Admin deletes a reservation | `ADMIN` |

Each entry records: entity name, entity ID, action, performer, timestamp, and a human-readable details string (e.g. `"Status changed from PENDING to CONFIRMED"`).

---

## Known Limitations

- No email/SMS reminder dispatch — reminders are stored but not sent
- No token revocation — logout is client-side only; JWT remains valid until 24h expiry
- No pagination — all list endpoints return full collections
- Audit logging covers reservations only; structure/service/slot/client changes are not logged
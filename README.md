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
| API Documentation | springdoc-openapi 2.7.0 (Swagger UI) |
| Frontend | Vanilla HTML/CSS/JS (served as static files) |

---

## Project Structure

```
src/main/java/com/grey/rdv_manager_api/
├── config/              OpenApiConfig (Swagger / JWT bearer scheme)
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
├── application.yml      Server, MongoDB, Jackson, JWT, springdoc config
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
- CORS configured inside Spring Security's filter chain (not as a separate `FilterRegistrationBean`)

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
| `/swagger-ui/**`, `/v3/api-docs/**` | Public (no token required) |

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

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: alpha
    tags-sorter: alpha
    try-it-out-enabled: true
  api-docs:
    path: /v3/api-docs
  show-actuator: false
  pre-loading-enabled: true
  packages-to-scan: com.grey.rdv_manager_api

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: rdv_manager_db_202606_test
  jackson:
    serialization:
      write-dates-as-timestamps: false

app:
  jwt:
    secret: <base64-encoded-secret-min-32-bytes>
    expiration-ms: 86400000   # 24 hours
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
    "roles": ["CLIENT"]
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

## API Documentation — Swagger UI

The project uses **springdoc-openapi 2.7.0** to auto-generate interactive API documentation.

> **Important:** Use version `2.7.0` specifically. Version `2.6.0` has a known incompatibility with Spring Boot 3.4.x that causes persistent `403 Forbidden` errors on all Swagger paths regardless of security configuration.

### Access URLs

| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Interactive Swagger UI |
| `http://localhost:8080/v3/api-docs` | Raw OpenAPI JSON |
| `http://localhost:8080/v3/api-docs.yaml` | Raw OpenAPI YAML |

### Using Swagger UI with JWT

1. Open `http://localhost:8080/swagger-ui.html`
2. Expand **Authentication** → `POST /api/auth/login` → click **Try it out**
3. Enter credentials and click **Execute**
4. Copy the `token` value from the response body (the long string only — not the word `Bearer`)
5. Click the **Authorize** button at the top of the page
6. Paste the token into the `bearerAuth` field and click **Authorize**
7. All subsequent requests in the UI will include the `Authorization: Bearer …` header automatically

### Endpoint Visibility

| Behaviour | Reason |
|---|---|
| `POST /api/auth/login` and `POST /api/auth/register` show no padlock | `security = {}` set on those operations — they are public |
| All other endpoints show a closed padlock 🔒 | `@SecurityRequirement(name = "bearerAuth")` set at controller level |
| Endpoints grouped by tag | `@Tag(name = "…")` annotation on each controller |

### CORS Note for Swagger UI

CORS is configured inside Spring Security's filter chain using `.cors(cors -> cors.configurationSource(...))` rather than a separate `FilterRegistrationBean`. This is required because a `FilterRegistrationBean` at `HIGHEST_PRECEDENCE` runs before Spring Security evaluates `permitAll()` rules, causing Swagger paths to receive `403` even when correctly whitelisted.

---

## API Testing — Postman

Postman is the recommended tool for testing the API outside of Swagger UI, especially for sequenced workflows like seeding data and confirming reservations.

### Setup

1. Download and install [Postman](https://www.postman.com/downloads/)
2. Open Postman → click **Import**
3. Select **Link** and enter: `http://localhost:8080/v3/api-docs`
4. Postman will auto-import all endpoints as a collection from the OpenAPI JSON

Alternatively, import as a raw file:
- Visit `http://localhost:8080/v3/api-docs` in your browser
- Save the JSON to a file (e.g. `rdv-api.json`)
- In Postman: **Import** → **File** → select `rdv-api.json`

### Authenticating with JWT

The cleanest approach is to use a Postman **Collection Variable** so every request gets the token automatically.

**Step 1 — Create a Login request**

```
Method : POST
URL    : http://localhost:8080/api/auth/login
Headers: Content-Type: application/json
Body (raw JSON):
{
  "email": "admin@rdv.com",
  "password": "Admin@1234"
}
```

**Step 2 — Auto-extract the token with a Test script**

In the **Tests** tab of the login request, add:

```javascript
const response = pm.response.json();
pm.collectionVariables.set("jwt_token", response.token);
pm.collectionVariables.set("client_id", response.id);
```

**Step 3 — Set the collection to use the token**

- Right-click your collection → **Edit**
- Go to the **Authorization** tab
- Set Type to **Bearer Token**
- Set Token to `{{jwt_token}}`
- Click **Save**

Every request in the collection now inherits this token automatically. You only need to re-run the login request when the token expires (24 hours).

### Environment Variables

Create a Postman environment (click the eye icon → **Add**) with these variables:

| Variable | Initial value | Description |
|---|---|---|
| `base_url` | `http://localhost:8080` | API base URL |
| `jwt_token` | _(auto-filled by login script)_ | Bearer token |
| `client_id` | _(auto-filled by login script)_ | Logged-in client UUID |
| `structure_id` | _(fill after creating a structure)_ | Reuse across service/slot requests |
| `service_id` | _(fill after creating a service)_ | Reuse across slot requests |
| `slot_id` | _(fill after creating a slot)_ | Reuse for reservation requests |
| `reservation_id` | _(fill after creating a reservation)_ | Reuse for confirm/cancel |

### Recommended Test Sequence

Run these requests in order to seed the system and test the full booking flow:

```
1.  POST   {{base_url}}/api/auth/login              → saves jwt_token, client_id
2.  POST   {{base_url}}/api/structures               → save returned id as structure_id
3.  POST   {{base_url}}/api/services                 → save returned id as service_id
4.  POST   {{base_url}}/api/slots                    → save returned id as slot_id
5.  GET    {{base_url}}/api/services                 → verify structureName is populated
6.  GET    {{base_url}}/api/slots                    → verify available = capacity
7.  POST   {{base_url}}/api/reservations             → save returned id as reservation_id
                                                       status should be PENDING
8.  GET    {{base_url}}/api/reservations/{{reservation_id}}
                                                     → confirm status = PENDING
9.  PUT    {{base_url}}/api/reservations/{{reservation_id}}
           body: { "status": "CONFIRMED" }           → slot.available should decrease by 1
10. GET    {{base_url}}/api/slots/{{slot_id}}        → verify available decreased
11. GET    {{base_url}}/api/audit-logs               → verify CREATE + UPDATE entries exist
12. PUT    {{base_url}}/api/reservations/{{reservation_id}}
           body: { "status": "CANCELLED" }           → slot.available should restore
13. GET    {{base_url}}/api/slots/{{slot_id}}        → verify available restored
14. DELETE {{base_url}}/api/reservations/{{reservation_id}}
                                                     → expect 204 No Content
```

### Sample Request Bodies

**POST /api/structures**
```json
{
  "name": "Klinik Cahaya",
  "description": "General health clinic",
  "address": "123 Jalan Ampang, Kuala Lumpur",
  "phone": "+60312345678",
  "email": "info@klinikcahaya.com"
}
```

**POST /api/services**
```json
{
  "structureId": "{{structure_id}}",
  "name": "General Consultation",
  "description": "Walk-in GP consultation"
}
```

**POST /api/slots**
```json
{
  "serviceId": "{{service_id}}",
  "date": "2026-07-01",
  "startTime": "09:00",
  "endTime": "09:30",
  "capacity": 5
}
```

**POST /api/reservations**
```json
{
  "clientId": "{{client_id}}",
  "slotId": "{{slot_id}}"
}
```

**PUT /api/reservations/{{reservation_id}}** — confirm
```json
{
  "status": "CONFIRMED"
}
```

**PUT /api/reservations/{{reservation_id}}** — cancel
```json
{
  "status": "CANCELLED"
}
```

### Common Response Codes

| Code | Meaning |
|---|---|
| `200 OK` | Successful GET or PUT |
| `201 Created` | Successful POST |
| `204 No Content` | Successful DELETE |
| `400 Bad Request` | Validation failed — check the `errors` array in the response body |
| `401 Unauthorized` | Missing or expired JWT — re-run the login request |
| `403 Forbidden` | Valid token but insufficient role (e.g. CLIENT accessing ADMIN endpoint) |
| `404 Not Found` | Entity does not exist |

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

Audit entries are written automatically for reservation and client lifecycle events:

| Action | Trigger | Performed by |
|---|---|---|
| `CREATE` | Client registers | Client email |
| `CREATE` | Client makes a booking | Client UUID |
| `UPDATE` | Admin confirms or cancels | `ADMIN` |
| `DELETE` | Admin deletes a reservation | `ADMIN` |

Each entry records: entity name, entity ID, action, performer, timestamp, and a human-readable details string.

---

## Known Limitations

- No email/SMS reminder dispatch — reminders are stored but not sent
- No token revocation — logout is client-side only; JWT remains valid until 24 h expiry
- No pagination — all list endpoints return full collections
- Audit logging covers reservations and client registration; structure/service/slot/client update changes are not logged

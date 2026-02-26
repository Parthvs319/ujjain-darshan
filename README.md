# Ujjain Darshan (Temple Trails)

Backend API for **Temple Trails** — a platform for temple tourism: trips, pujaris, drivers, and hotels in Ujjain.

**Stack:** Java 17, Vert.x 4, RxJava 2, Ebean ORM, MySQL, JWT (Auth0).

---

## Project structure

Multi-module Maven project:

| Module   | Description                          |
|----------|--------------------------------------|
| `helpers`| Shared utilities, middleware, SQL     |
| `models` | Entities, repos, DTOs, services      |
| `user`   | Sign up, login, user management      |
| `auth`   | JWT tokens, OTP, refresh             |
| `tourist`| Trip creation, list trips            |
| `hotel`  | List property, hotels, verification  |
| `pujari` | Pujari details, trip requests        |
| `driver` | Driver/vehicle onboarding, trip requests |
| `app`    | Main application & HTTP server      |

---

## Requirements

- **Java 17**
- **Maven 3.6+**
- **MySQL 8** (or compatible)

---

## Build

From the project root:

```bash
mvn clean package -DskipTests
```

Runnable fat JAR:

```text
app/target/app-1.0.0.jar
```

---

## Run

**Using the JAR:**

```bash
java -jar app/target/app-1.0.0.jar
```

**Using Maven:**

```bash
mvn -pl app exec:java
```

Server listens on **port 8080** by default.

---

## Environment variables

### Required (database)

Used by `SqlConfigFactory`; typical for Railway/Render-style MySQL:

| Variable        | Description           | Example / default   |
|----------------|-----------------------|----------------------|
| `MYSQLHOST`    | MySQL host            | `localhost`          |
| `MYSQLPORT`    | MySQL port            | `3306`               |
| `MYSQLDATABASE`| Database name         | `ujjain-darshan-db` |
| `MYSQLUSER`    | Database user         | `root`               |
| `MYSQLPASSWORD`| Database password     | —                    |

If these are not set, the app falls back to local defaults (see `helpers/sql/SqlConfigFactory.java`).

### Optional

| Variable   | Description              | Default   |
|-----------|---------------------------|-----------|
| `PORT`    | HTTP server port          | `8080`    |
| `JWT_SECRET` | JWT signing secret (if used) | (in-code default) |

### WhatsApp (optional)

Used by `models.services.WhatsAppService` for trip/pujari/driver notifications:

- `WHATSAPP_API_URL` — WhatsApp API base URL  
- `WHATSAPP_API_KEY` — API key  
- `WHATSAPP_PHONE_NUMBER_ID` — Phone number ID (Business API)  
- `WHATSAPP_FROM_NUMBER` — Sender name (e.g. `Temple Trails`)

If not set, messages are only logged.

---

## API overview

Base URL: `http://localhost:8080` (or your `PORT`).

Protected routes expect:

```http
Authorization: Bearer <access_token>
```

### Health

- **GET** `/health` — Liveness (returns `OK`).

### User — `/user`

- **POST** `/user/signUp` — Register (mobile, password, name, userType, …).
- **POST** `/user/login` — Login (mobile/email + password or OTP); returns JWT.
- **GET** `/user/getAllUsers` — List users (protected).
- **GET** `/user/:type/getAllUsers` — List users by type (protected).
- **POST** `/user/deactivateUser` — Deactivate user (protected).
- **POST** `/user/activateUser` — Activate user (protected).

### Auth — `/auth`

- **POST** `/auth/getToken` — Get bearer token by `userId` (protected).
- **POST** `/auth/refresh` — Refresh access token.
- **POST** `/auth/otp/generate` — Generate OTP for a user.
- **POST** `/auth/otp/verify` — Verify OTP.

### Tourist — `/tourist`

- **POST** `/tourist/createTrip` — Create a trip (body: e.g. `cityId`, `details` with dates, stay/travel, temples/pujas). Protected; user type `TOURIST`.
- **GET** `/tourist/getAllTrips` — List trips (protected).

### Hotel — `/hotel`

- **POST** `/hotel/listProperty` — List a property (body: `name`, `lat`, `lng`, `cityId`, `details` with images, rooms, amenities). Protected; user type `HOTEL_ADMIN`. At least 5 images required in `details.images`.
- **GET** `/hotel/getAllHotels` — List all hotels.
- **GET** `/hotel/:id/getAllHotelsByUser` — List hotels by user (protected).
- **DELETE** `/hotel/:id/remove` — Remove hotel (protected).
- **POST** `/hotel/:id/verifyByAdmin` — Verify hotel (protected).
- **POST** `/hotel/activate` — Activate hotel (protected).

### Pujari — `/pujari`

- **POST** `/pujari/completeDetails` — Submit/update pujari details (protected).
- **POST** `/pujari/:id/verifyByAdmin` — Verify pujari (protected).
- **GET** `/pujari/getAllPujari` — List pujaris.
- **GET** `/pujari/details` — Pujari details (protected).
- **GET** `/pujari/getTripRequests` — Pujari’s trip requests (protected).
- **POST** `/pujari/acceptTripRequest` — Accept trip request (body: `requestId`) (protected).
- **POST** `/pujari/rejectTripRequest` — Reject trip request (protected).

### Driver — `/driver`

- **POST** `/driver/completeOnboarding` — Complete driver onboarding (protected).
- **GET** `/driver/getOnboardingDetails` — Get onboarding details (protected).
- **GET** `/driver/getAllDrivers` — List drivers.
- **POST** `/driver/approve/driver` — Approve driver (protected).
- **GET** `/driver/approve/vehicle` — Approve vehicle (protected).
- **GET** `/driver/getAllVehicles` — List vehicles.
- **GET** `/driver/:id/getAllVehiclesByUser` — Vehicles by user (protected).
- **POST** `/driver/addVehicle` — Add vehicle (protected).
- **DELETE** `/driver/:id/remove` — Remove driver (protected).
- **GET** `/driver/getTripRequests` — Driver’s trip requests (protected).
- **POST** `/driver/acceptTripRequest` — Accept trip request (body: `requestId`, optional `vehicleIds`) (protected).
- **POST** `/driver/rejectTripRequest` — Reject trip request (protected).

---

## User types

Used in sign-up and authorization:

- `TOURIST`
- `PUJARI`
- `GUIDE`
- `DRIVER`
- `TEMPLE_ADMIN`
- `HOTEL_ADMIN`
- `ADMIN`

---

## Sample payloads

- **List property (hotel):** see `listProperty_sample_payload.json` and `listProperty_clean_payload.json` in the repo root. Required: `name`, `lat`, `lng`, `cityId`, and `details` with at least 5 `images`.

---

## Notifications

When WhatsApp env vars are set, the app sends WhatsApp messages (via `models.services.WhatsAppService`):

- After **trip creation** — tourist gets trip summary.
- After **pujari acceptance** — pujari and tourist get trip/pujari details.
- After **driver acceptance** — driver and tourist get trip/driver details.

---

## License

Proprietary / as per your project.

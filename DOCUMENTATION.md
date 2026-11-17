# TrackMyJob API — Complete Project Documentation

> **Who is this for?**  
> This document is written for beginners and junior developers. Every concept is explained from scratch in simple English with real-life analogies. If you built this project and want to understand every line deeply — or prepare for interviews — this is your complete guide.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Project Structure Explained](#2-project-structure-explained)
3. [Phase 1 — Setup & Configuration](#3-phase-1--setup--configuration)
4. [Phase 2 — Database Design & JPA Entities](#4-phase-2--database-design--jpa-entities)
5. [Phase 3 — Repository, Service & Controller Layers](#5-phase-3--repository-service--controller-layers)
6. [Phase 4 — Spring Security & JWT Authentication](#6-phase-4--spring-security--jwt-authentication)
7. [Phase 5 — Docker & Docker Compose](#7-phase-5--docker--docker-compose)
8. [Phase 6 — Exception Handling & Validation](#8-phase-6--exception-handling--validation)
9. [Phase 7 — Swagger/OpenAPI Documentation](#9-phase-7--swaggeropenapi-documentation)
10. [Phase 8 — Testing & CI/CD](#10-phase-8--testing--cicd)
11. [Complete Interview Q&A](#11-complete-interview-qa)

---

## 1. Project Overview

### What Does This Project Do?

TrackMyJob is a **REST API** (a backend service) that helps users track their job applications. Think of it like a digital diary for your job search — you can record which companies you applied to, what role you applied for, the current status (applied, interview scheduled, rejected, etc.), and any personal notes.

### What is a REST API?

Imagine a restaurant. You (the client) sit at a table and give your order to a waiter. The waiter (the API) takes your order to the kitchen (the server/database), and brings back your food (the response). You never go directly to the kitchen.

A REST API works the same way:
- **Client** (Postman, mobile app, browser) sends a request
- **API** (our Spring Boot app) processes it
- **Database** (PostgreSQL) stores or retrieves the data
- **API** sends back a response (JSON format)

### Why Was This Built?

This project demonstrates real-world backend development skills for portfolio and interview purposes. It covers every concern a production Java backend has:

| Concern | What We Built |
|---|---|
| Authentication | JWT-based login/register |
| Data storage | PostgreSQL with JPA/Hibernate |
| Security | Spring Security filter chain |
| Error handling | Global exception handler |
| Documentation | Swagger UI |
| Containerization | Docker Compose |
| Testing | JUnit 5 + Mockito |
| CI/CD | GitHub Actions |

---

## 2. Project Structure Explained

```
TrackMyJob/
├── src/
│   ├── main/
│   │   ├── java/com/trackmyjob/
│   │   │   ├── config/          ← App-wide configuration classes
│   │   │   ├── controller/      ← Handles HTTP requests and responses
│   │   │   ├── dto/             ← Data shapes for request and response
│   │   │   │   ├── request/     ← What the client sends us
│   │   │   │   └── response/    ← What we send back to the client
│   │   │   ├── entity/          ← Java classes that map to DB tables
│   │   │   ├── exception/       ← Custom errors and global error handler
│   │   │   ├── repository/      ← Database operations (Spring Data JPA)
│   │   │   ├── security/        ← JWT logic, filters, user loading
│   │   │   └── service/         ← Business logic
│   │   │       └── impl/        ← Concrete implementations of services
│   │   └── resources/
│   │       ├── application.properties         ← Main config
│   │       └── application-docker.properties  ← Docker-specific config
│   └── test/
│       ├── java/com/trackmyjob/
│       │   ├── controller/      ← Integration tests
│       │   └── service/         ← Unit tests
│       └── resources/
│           └── application-test.properties    ← Test config (H2 DB)
├── .github/
│   └── workflows/
│       └── ci.yml               ← GitHub Actions pipeline
├── docker-compose.yml           ← Defines all Docker containers
├── Dockerfile                   ← How to build the app container
└── pom.xml                      ← Maven dependencies
```

### The Layered Architecture — The Most Important Concept

Every professional Spring Boot project follows this pattern:

```
HTTP Request
     ↓
Controller  ← "Receptionist" — receives requests, sends responses
     ↓
Service     ← "Manager" — makes decisions, handles business logic
     ↓
Repository  ← "Clerk" — fetches and saves data in the database
     ↓
Database    ← "Filing cabinet" — stores everything permanently
```

**Why separate layers?** Same reason a hospital has separate departments. The receptionist doesn't perform surgery. The surgeon doesn't manage billing. Each person has one job, does it well, and hands off to the next person. This is called **Separation of Concerns**.

---

## 3. Phase 1 — Setup & Configuration

### `pom.xml` — The Project's Shopping List

`pom.xml` is the Maven configuration file. It lists every library (dependency) your project needs. Maven automatically downloads them from the internet.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>
```

**What this does:** By declaring Spring Boot as the "parent", we inherit pre-tested, compatible versions for all common libraries. Think of it as buying a furniture set — everything matches, nothing conflicts.

**Key dependencies explained:**

| Dependency | Real-World Analogy |
|---|---|
| `spring-boot-starter-web` | The waiter — handles HTTP communication |
| `spring-boot-starter-data-jpa` | The translator — converts Java to SQL |
| `spring-boot-starter-security` | The security guard — protects all endpoints |
| `postgresql` | The phone line — connects Java to PostgreSQL |
| `lombok` | The assistant — writes boilerplate code for you |
| `jjwt-api` | The ID card machine — creates and reads JWT tokens |
| `springdoc-openapi` | The menu board — shows all available API endpoints |

### `application.properties` — The Control Panel

```properties
server.port=8080
```
Tells Spring Boot which port to listen on. Port 8080 means clients connect at `http://localhost:8080`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trackmyjob_db
spring.datasource.username=trackmyjob_user
spring.datasource.password=trackmyjob_pass
```
Database connection details. This is how Spring Boot finds and logs into PostgreSQL.

```properties
spring.jpa.hibernate.ddl-auto=update
```
Controls what Hibernate does with the database schema on startup:
- `update` — creates tables if they don't exist, adds new columns if needed. **Used in development.**
- `validate` — only checks that tables match entities. **Used in production.**
- `create-drop` — drops and recreates everything. **Used in testing.**

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
Prints every SQL statement Hibernate runs in the console. Invaluable for learning. Turn this off in production.

```properties
jwt.secret=dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5...
jwt.expiration=86400000
```
The secret key for signing JWT tokens (explained in detail in Phase 4). Expiration is in milliseconds — 86,400,000ms = 24 hours.

### `TrackMyJobApplication.java` — The Entry Point

```java
@SpringBootApplication
public class TrackMyJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackMyJobApplication.class, args);
    }
}
```

`@SpringBootApplication` is actually three annotations combined:

1. **`@SpringBootConfiguration`** — marks this as a configuration class
2. **`@EnableAutoConfiguration`** — tells Spring Boot to auto-configure beans based on what's in the classpath (if `spring-boot-starter-web` is present, configure a web server automatically)
3. **`@ComponentScan`** — scans `com.trackmyjob` and all sub-packages for `@Component`, `@Service`, `@Repository`, `@Controller` annotated classes and registers them as Spring beans

**What is a Spring Bean?** An object that Spring creates and manages for you. Instead of writing `new UserService()` everywhere, you annotate the class with `@Service` and Spring creates one instance and shares it wherever needed. This is **Dependency Injection**.

---

## 4. Phase 2 — Database Design & JPA Entities

### What is JPA and Hibernate?

**JPA (Jakarta Persistence API)** is a specification (a set of rules) for how Java should talk to databases.

**Hibernate** is the most popular implementation of JPA. It actually does the work.

Think of JPA as a job description ("the employee must be able to drive") and Hibernate as the actual employee who can drive.

With Hibernate, instead of writing:
```sql
INSERT INTO users (name, email, password) VALUES (?, ?, ?)
```
You just write:
```java
userRepository.save(user);
```
Hibernate generates and runs the SQL for you.

### `BaseEntity.java` — The Parent Template

```java
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**`@MappedSuperclass`** — tells JPA: "This class is NOT its own database table. Its fields are inherited by child classes and added to THEIR tables." Like a template that children copy from.

**`@PrePersist`** — a JPA lifecycle callback. This method runs automatically BEFORE a new record is inserted (INSERT). We set both timestamps here.

**`@PreUpdate`** — runs automatically BEFORE an UPDATE. We only update `updatedAt` — `createdAt` stays fixed forever.

**Why not use `@CreationTimestamp` from Hibernate?** In Hibernate 6.6 (used in Spring Boot 3.5.0), combining `@CreationTimestamp` with `@Column(updatable=false)` causes a conflict — both register as `@ValueGenerationType` annotations. Using JPA lifecycle callbacks (`@PrePersist`/`@PreUpdate`) avoids this conflict and is also more portable (pure JPA, no Hibernate-specific annotations).

### `ApplicationStatus.java` — The Enum

```java
public enum ApplicationStatus {
    APPLIED,
    INTERVIEW_SCHEDULED,
    OFFER_RECEIVED,
    REJECTED,
    WITHDRAWN
}
```

An **enum** is a fixed set of named constants. A job application's status can only ever be one of these five values — nothing else. This is enforced at both the Java level (compile error if you use something else) and the database level (PostgreSQL CHECK constraint is auto-generated).

**Why store as STRING and not a number?**

If we stored as ordinal (0, 1, 2...) and later added a new status in the middle, all the numbers would shift — corrupting existing data. Storing the name ("APPLIED") is always safe regardless of order changes.

### `User.java` — The User Entity

```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications = new ArrayList<>();
}
```

**`@Entity`** — tells Hibernate: "Map this Java class to a database table."

**`@Table(name = "users")`** — specifies the exact table name. We use "users" (not "user") because "user" is a reserved keyword in PostgreSQL and causes SQL errors.

**`@Id`** — marks the primary key field.

**`@GeneratedValue(strategy = GenerationType.IDENTITY)`** — lets the database auto-generate IDs. PostgreSQL uses `BIGSERIAL` (auto-increment). Every new row gets the next available number automatically.

**`@Column(nullable = false, unique = true, length = 150)`** — adds constraints:
- `nullable = false` → NOT NULL constraint in DB
- `unique = true` → UNIQUE constraint (no two users with same email)
- `length = 150` → VARCHAR(150) in DB

**`@OneToMany`** — one user has many job applications. This is the relationship definition.
- `mappedBy = "user"` — the foreign key column lives in the `job_applications` table (not here). The `"user"` refers to the field name in `JobApplication`.
- `cascade = CascadeType.ALL` — if we delete a user, delete all their applications too.
- `orphanRemoval = true` — if an application is removed from the list, delete it from DB.

**Why `@Getter`/`@Setter` instead of `@Data`?**

`@Data` generates `toString()` that follows relationships. With bidirectional relationships (User → JobApplication → User → ...), `toString()` causes infinite recursion and a `StackOverflowError`. Using explicit `@Getter`, `@Setter`, `@ToString(exclude="jobApplications")` gives full control.

### `JobApplication.java` — The Main Entity

```java
@Entity
@Table(name = "job_applications")
public class JobApplication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status;
}
```

**`@ManyToOne`** — many applications belong to one user. This is the "many" side.

**`fetch = FetchType.LAZY`** — don't load the full User object every time we load a JobApplication. Load it only when explicitly accessed. This is critical for performance — loading a User every time you need an application wastes database queries.

**`@JoinColumn(name = "user_id")`** — creates the foreign key column named `user_id` in the `job_applications` table. This column stores the ID of the user who owns this application.

**`@Enumerated(EnumType.STRING)`** — stores the enum NAME (not its number) in the database.

### The Relationship — Visualized

```
users table                    job_applications table
──────────────                 ──────────────────────────
id = 1                         id = 1, user_id = 1 ─────┐
name = Ankith                  id = 2, user_id = 1 ─────┤── all belong to user 1
email = a@b.com                id = 3, user_id = 2      │
                               id = 4, user_id = 2      │
id = 2                         ──────────────────────────┘
name = Ravi
```

---

## 5. Phase 3 — Repository, Service & Controller Layers

### DTOs — Why We Never Expose Entities Directly

**DTO (Data Transfer Object)** — a simple class that carries data between layers.

**Why not just use the Entity?**

Imagine you work at a bank. When a customer asks for their account details, you don't hand them the entire internal account record — that might contain internal audit codes, admin flags, and other sensitive internal data. You give them a printed statement (DTO) with only what they need to see.

Same with our API. The `User` entity has `password` (even hashed, we don't expose it). The `UserResponse` DTO has only `name` and `email`.

```java
// What the client sends us (Request DTO)
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}

// What we send back (Response DTO)
public class AuthResponse {
    private String message;
    private String email;
    private String name;
    private String token;
}
```

### `UserRepository.java` — The Database Interface

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

This is one of the most powerful features of Spring Data JPA. You write an **interface** — Spring generates the full implementation at runtime. No SQL needed for basic operations.

`JpaRepository<User, Long>` gives you for FREE:
- `save(user)` → INSERT or UPDATE
- `findById(id)` → SELECT WHERE id = ?
- `findAll()` → SELECT * FROM users
- `deleteById(id)` → DELETE WHERE id = ?
- `count()` → SELECT COUNT(*)
- `existsById(id)` → SELECT EXISTS(...)

**Query Derivation** — Spring reads the method name and generates SQL:

```java
findByEmail(String email)
// → SELECT * FROM users WHERE email = ?

existsByEmail(String email)
// → SELECT EXISTS(...) WHERE email = ?

findByIdAndUserId(Long id, Long userId)
// → SELECT * FROM job_applications WHERE id = ? AND user_id = ?
```

This is called "query derivation" — one of the most asked-about features in Spring Boot interviews.

### `AuthServiceImpl.java` — The Business Logic

```java
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
```

**`@Service`** — marks this as a Spring-managed service bean. Spring creates one instance and injects it wherever `AuthService` is needed.

**`@RequiredArgsConstructor`** (Lombok) — generates a constructor for all `final` fields. Spring sees this constructor and injects the dependencies automatically. This is **Constructor Injection** — the recommended way to inject dependencies.

**Why Constructor Injection and not `@Autowired` on fields?**
- Makes dependencies explicit (you can see what a class needs just by reading its constructor)
- Makes unit testing easy (just pass mock objects to the constructor)
- Prevents circular dependency issues at startup

```java
@Override
public AuthResponse register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
        throw new EmailAlreadyExistsException(request.getEmail());
    }

    User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

    User savedUser = userRepository.save(user);
    String token = jwtUtil.generateToken(savedUser.getEmail());

    return AuthResponse.builder()
            .message("Registration successful")
            .email(savedUser.getEmail())
            .name(savedUser.getName())
            .token(token)
            .build();
}
```

Notice: we NEVER store the plain password. `passwordEncoder.encode("password123")` converts it to a BCrypt hash like `$2a$10$xxxxxxxxxxx`. This is a one-way operation — you can never get the original back. That's intentional.

### `AuthController.java` — The HTTP Handler

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**`@RestController`** = `@Controller` + `@ResponseBody`. Every method returns data (JSON), not an HTML view.

**`@RequestMapping("/api/auth")`** — all endpoints in this controller are prefixed with `/api/auth`.

**`@Valid`** — triggers Bean Validation on the `RegisterRequest` object. If any `@NotBlank`, `@Email`, `@Size` checks fail, Spring automatically returns a 400 error before the method even runs.

**`@RequestBody`** — tells Spring to read the HTTP request body and convert the JSON into a `RegisterRequest` Java object.

**`ResponseEntity`** — lets us control the HTTP status code. `HttpStatus.CREATED` = 201. For reads we use `ResponseEntity.ok()` = 200. For deletes we use `ResponseEntity.noContent().build()` = 204.

**HTTP Status Codes used in this project:**
| Code | Meaning | When We Use It |
|---|---|---|
| 200 OK | Success | GET, login |
| 201 Created | New resource created | POST (create) |
| 204 No Content | Success with no body | DELETE |
| 400 Bad Request | Client sent invalid data | Validation failures |
| 401 Unauthorized | Identity not verified | Wrong credentials, missing token |
| 403 Forbidden | Identity verified but no permission | Token valid but endpoint restricted |
| 404 Not Found | Resource doesn't exist | Application/user not found |
| 409 Conflict | Request conflicts with existing data | Duplicate email |
| 500 Internal Server Error | Unexpected server error | Uncaught exceptions |

---

## 6. Phase 4 — Spring Security & JWT Authentication

### What is JWT?

**JWT (JSON Web Token)** is a way to prove your identity without the server remembering you.

**Real-life analogy:** A movie ticket. When you buy a ticket, the cinema gives you a printed ticket with your seat number and movie name. At the entry, the guard checks your ticket — they don't need to look you up in any system. The ticket itself contains the proof. JWT works the same way.

### JWT Structure

A JWT looks like this:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmtpdGhAZXhhbXBsZS5jb20ifQ.xK8s9...
        ↑                          ↑                              ↑
    HEADER                      PAYLOAD                       SIGNATURE
  (algorithm)              (email, expiry)              (digital seal)
```

Three parts separated by dots, Base64 encoded:
- **Header** — which algorithm was used (HS256)
- **Payload** — the actual data (who is this user, when does the token expire)
- **Signature** — the digital seal created using our secret key

**Important:** JWT is encoded, NOT encrypted. Anyone can decode the header and payload. The SIGNATURE is what makes it trustworthy — only our server (which knows the secret key) can create a valid signature.

### `JwtUtil.java` — The Token Machine

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }
}
```

**`@Value("${jwt.secret}")`** — reads the value from `application.properties`. Spring injects it directly into this field. This is how we avoid hardcoding secrets in Java code.

**`getSigningKey()`** — converts the Base64 secret string into a cryptographic `SecretKey` object that JJWT uses.

**`generateToken(email)`** — creates a signed JWT:
1. Sets the subject (who this token is for) = email
2. Sets issued time = now
3. Sets expiry = now + 24 hours
4. Signs it with our secret key
5. Compacts it into the three-part string

**`validateToken(token, email)`** — checks two things:
1. Does the email in the token match the expected email?
2. Is the token not expired?

### `CustomUserDetailsService.java` — The Bridge

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
```

Spring Security doesn't know about our `User` entity. It works with its own `UserDetails` interface. This class is the bridge — it loads our user from the database and wraps it in the format Spring Security understands.

Note: `org.springframework.security.core.userdetails.User` (Spring's class) is different from `com.trackmyjob.entity.User` (our class). Both exist in this project — the full package name is used to avoid confusion.

### `JwtAuthFilter.java` — The Security Guard

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

**`OncePerRequestFilter`** — guarantees this filter runs exactly once per request (Spring can call filters multiple times internally; this prevents that).

**The flow on every request:**
1. Read the `Authorization` header
2. If it doesn't start with "Bearer " → skip (the request continues without authentication set)
3. Extract the token (remove "Bearer " — 7 characters)
4. Extract email from token
5. Load user from database
6. Validate the token
7. If valid → create `UsernamePasswordAuthenticationToken` and set it in `SecurityContextHolder`
8. Continue the filter chain

**`SecurityContextHolder`** — Spring's storage for "who is the current user?" For the duration of this request, once we set authentication here, Spring Security treats the request as authenticated. The `@AuthenticationPrincipal` annotation in controllers reads from here.

### `SecurityConfig.java` — The Rules

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/swagger-ui/**", "/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**`csrf.disable()`** — CSRF (Cross-Site Request Forgery) protection is needed for browser-based form submissions that use cookies. Our API uses JWT in headers — no CSRF risk — so we disable it.

**`authorizeHttpRequests`** — defines which endpoints are public vs protected:
- `/api/auth/**` → anyone can register and login (no token needed)
- `anyRequest().authenticated()` → everything else needs a valid token

**`SessionCreationPolicy.STATELESS`** — no server-side sessions. Every request must carry its own JWT. This makes the API scalable — any server instance can handle any request because there's no session state to share.

**`addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`** — inserts our JWT filter before Spring's default username/password filter. Our filter must authenticate the user first, otherwise Spring would redirect to a login page.

### The Complete Authentication Flow

```
Registration:
Client → POST /api/auth/register {name, email, password}
       → AuthController.register()
       → AuthServiceImpl: check duplicate email, hash password, save user
       → JwtUtil.generateToken(email)
       → Return {token, email, name, message}

Login:
Client → POST /api/auth/login {email, password}
       → AuthController.login()
       → AuthServiceImpl: find user by email, verify BCrypt hash
       → JwtUtil.generateToken(email)
       → Return {token, email, name, message}

Protected Request:
Client → GET /api/applications
         Header: Authorization: Bearer eyJhbGci...
       → JwtAuthFilter: extract token, validate, set authentication
       → SecurityContextHolder has authentication
       → JobApplicationController: @AuthenticationPrincipal extracts email
       → getCurrentUserId(email) → look up userId from DB
       → jobApplicationService.getAllByUser(userId)
       → Return list of applications
```

---

## 7. Phase 5 — Docker & Docker Compose

### What is Docker?

**Without Docker:** Install Java 21 → Install PostgreSQL → Configure PostgreSQL → Set environment variables → Hope nothing conflicts with other software.

**With Docker:** `docker compose up`. Done.

Docker packages your application and everything it needs (Java runtime, PostgreSQL, config) into a **container** — a sealed, isolated box that runs identically on any machine.

**Real-life analogy:** A lunchbox. You pack everything your lunch needs — food, cutlery, napkins. You can take that lunchbox anywhere and have your complete lunch without needing a restaurant.

### `Dockerfile` — Recipe for the App Container

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
```

**Two-stage build — why?**

The JDK (Java Development Kit) includes the compiler and is ~600MB. The JRE (Java Runtime Environment) only includes what's needed to run the app and is ~200MB. We build in Stage 1 (need the compiler), then copy only the JAR to Stage 2 (just need the runtime). Final image is ~200MB instead of ~600MB.

**Layer caching** — Docker processes each instruction as a layer:
```dockerfile
COPY pom.xml .
RUN ./mvnw dependency:go-offline  # ← cached unless pom.xml changes
COPY src src
RUN ./mvnw package                # ← only rebuilds when source changes
```
If you only change Java code (not `pom.xml`), Docker reuses the dependency download layer and rebuilds in seconds instead of minutes.

### `docker-compose.yml` — The Orchestra Conductor

```yaml
services:

  postgres:
    image: postgres:16-alpine
    container_name: trackmyjob_postgres
    environment:
      POSTGRES_DB: trackmyjob_db
      POSTGRES_USER: trackmyjob_user
      POSTGRES_PASSWORD: trackmyjob_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U trackmyjob_user -d trackmyjob_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/trackmyjob_db
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

**Key concepts:**

`ports: "5432:5432"` → HOST_PORT:CONTAINER_PORT. Left side is your machine, right side is inside Docker. When your app connects to `localhost:5432`, Docker forwards it into the container.

`volumes: postgres_data` → A named volume. Containers are stateless — all data is deleted when the container is removed. Volumes store data outside the container, so your database survives restarts.

`healthcheck` → Before starting the app container, Docker checks if PostgreSQL is actually ready to accept connections (not just started). The app waits until the database passes the health check.

`depends_on: condition: service_healthy` → The app container starts ONLY after PostgreSQL is healthy. Without this, the app might try to connect before the database is ready and fail.

**Why `postgres` in the URL instead of `localhost`?**
Inside Docker, containers talk to each other using service names, not `localhost`. Docker's internal DNS resolves `postgres` to the database container's IP address automatically.

### Spring Profiles

```
application.properties          ← always loaded (base config)
application-docker.properties   ← loaded only when profile=docker
application-test.properties     ← loaded only when profile=test
```

When `ENTRYPOINT` passes `-Dspring.profiles.active=docker`, Spring Boot loads both `application.properties` AND `application-docker.properties`. The docker file overrides only what's different (the datasource URL).

---

## 8. Phase 6 — Exception Handling & Validation

### Why Custom Exception Handling?

Without it, every error returns:
```json
{"status": 500, "error": "Internal Server Error"}
```

The client has no idea what went wrong. With `@RestControllerAdvice`:
```json
{
  "status": 409,
  "message": "Email already registered: ankith@example.com",
  "timestamp": "2026-09-10T12:00:00"
}
```

Clear, specific, useful.

### Custom Exception Classes

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**Why `RuntimeException`?** It's unchecked — you don't need to declare it in method signatures. Cleaner code. Spring handles it in the global exception handler.

**Why annotate with `@ResponseStatus`?** Documents the intended HTTP status. The actual status is set in the `GlobalExceptionHandler`, but this annotation also serves as documentation.

Each custom exception maps to the correct HTTP status:
- `ResourceNotFoundException` → 404 Not Found
- `EmailAlreadyExistsException` → 409 Conflict
- `InvalidCredentialsException` → 401 Unauthorized

### `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .status(400)
                .message("Validation failed")
                .errors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

**`@RestControllerAdvice`** — a special class that intercepts exceptions from ALL `@RestController` classes. Think of it as a safety net wrapped around every controller.

**`@ExceptionHandler(MethodArgumentNotValidException.class)`** — this method runs when `@Valid` fails on a request body. Spring throws `MethodArgumentNotValidException` automatically.

**`getBindingResult().getAllErrors()`** — gives us all the validation failures as a list. We convert them into a map: `{"email": "must be valid", "password": "too short"}`.

**`@JsonInclude(NON_NULL)` on `ErrorResponse`** — if the `errors` field is null (not a validation error), it's excluded from the JSON output completely. Response stays clean.

### Bean Validation Annotations

```java
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}
```

These annotations are processed BEFORE the controller method runs. If any fail, a `400 Bad Request` is returned automatically. Your service layer never sees invalid data.

| Annotation | What It Checks |
|---|---|
| `@NotBlank` | Not null, not empty, not just spaces |
| `@NotNull` | Not null (but can be empty — use for non-String types) |
| `@Email` | Valid email format |
| `@Size(min, max)` | String length |

---

## 9. Phase 7 — Swagger/OpenAPI Documentation

### What is Swagger?

Swagger UI is an interactive web page that shows all your API endpoints and lets you test them directly in the browser. No Postman needed.

Access it at: `http://localhost:8080/swagger-ui/index.html`

### `SwaggerConfig.java`

```java
@OpenAPIDefinition(
    info = @Info(title = "TrackMyJob API", version = "1.0.0"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class SwaggerConfig {}
```

**`@OpenAPIDefinition`** — defines the top-level metadata shown at the top of Swagger UI.

**`security = @SecurityRequirement(name = "bearerAuth")`** — applies JWT authentication globally. The 🔒 Authorize button appears in the UI where you paste your token.

**`@SecurityScheme`** — defines HOW authentication works (Bearer token in Authorization header).

### Controller Annotations

```java
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    @Operation(summary = "Register a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<AuthResponse> register(...) { }
}
```

**`@Tag`** — groups endpoints in the UI. All controller methods appear under "Authentication".

**`@Operation`** — description shown in the UI for each endpoint.

**`@ApiResponse`** — documents what each HTTP status code means for this endpoint. Helps API consumers understand what to expect.

---

## 10. Phase 8 — Testing & CI/CD

### Testing Strategy

We use two layers of tests:

```
Unit Tests (fast, isolated)
    ↑ mock all dependencies
    ↑ test business logic only
    ↑ run in milliseconds

Integration Tests (slower, real)
    ↑ full Spring context
    ↑ real HTTP calls via MockMvc
    ↑ real database (H2 in-memory)
    ↑ test that everything works together
```

### Unit Tests with Mockito

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;    // fake UserRepository

    @Mock
    private PasswordEncoder passwordEncoder;  // fake PasswordEncoder

    @InjectMocks
    private AuthServiceImpl authService;      // real AuthServiceImpl, with fakes injected
```

**`@Mock`** — creates a fake object that does nothing by default. You control exactly what it returns.

**`@InjectMocks`** — creates a real instance of `AuthServiceImpl` and injects all `@Mock` fields into it.

**Why mock?** We're testing `AuthServiceImpl`'s logic, not `UserRepository`. We tell the mock "when `findByEmail` is called, return this user" — so we test only what `AuthServiceImpl` does with that user, not whether the database query works.

```java
@Test
void register_DuplicateEmail_ThrowsEmailAlreadyExistsException() {
    // ARRANGE — set up mock behaviour
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // ACT & ASSERT — call the method and expect the exception
    assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(EmailAlreadyExistsException.class);

    // VERIFY — save should never be called when email exists
    verify(userRepository, never()).save(any(User.class));
}
```

**AAA Pattern** — every test follows Arrange, Act, Assert:
1. **Arrange** — set up the test data and mock behaviour
2. **Act** — call the method being tested
3. **Assert** — verify the result was correct

### Integration Tests with MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_ValidRequest_Returns201WithToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is("ankith@example.com")));
    }
}
```

**`@SpringBootTest`** — loads the full Spring application context. Real beans, real security, real service layer.

**`@AutoConfigureMockMvc`** — sets up MockMvc. We send HTTP requests without starting a real server — faster, but tests the full Spring MVC stack.

**`@ActiveProfiles("test")`** — activates `application-test.properties` which uses H2 in-memory database instead of PostgreSQL.

**`jsonPath("$.token")`** — verifies a specific field in the JSON response using JSONPath syntax.

### GitHub Actions CI/CD

```yaml
name: TrackMyJob CI
on:
  push:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - run: mvn clean verify -B
```

**What this does:** Every time you push code to the `main` branch, GitHub:
1. Starts a fresh Ubuntu virtual machine
2. Installs Java 21
3. Downloads your project
4. Runs `mvn clean verify` — compiles the code, runs all tests
5. Shows green ✅ or red ❌

This means broken code never silently enters your main branch undetected.

---

## 11. Complete Interview Q&A

### General Spring Boot Questions

**Q: What is Spring Boot and how is it different from Spring Framework?**

A: Spring Framework is a large framework with lots of modules (Spring MVC, Spring Data, Spring Security). You had to configure everything manually — XML files, bean definitions, server setup. Spring Boot is a tool built on top of Spring Framework that adds auto-configuration. It reads what's on your classpath and configures things automatically. If `spring-boot-starter-web` is present, it sets up Tomcat and Spring MVC. If `spring-boot-starter-data-jpa` is present, it sets up Hibernate and a DataSource. You focus on business logic, not configuration.

**Q: What does `@SpringBootApplication` do?**

A: It's a composed annotation that combines three things:
1. `@SpringBootConfiguration` — marks this as a configuration class
2. `@EnableAutoConfiguration` — activates auto-configuration based on classpath
3. `@ComponentScan` — scans the current package and all sub-packages for Spring components (`@Service`, `@Repository`, `@Controller`, etc.) and registers them as beans

**Q: What is Dependency Injection and how does Spring implement it?**

A: Dependency Injection means you don't create your dependencies — you declare them and something else creates and provides them. In our project, `AuthServiceImpl` needs a `UserRepository`. Instead of writing `new UserRepository()` inside `AuthServiceImpl`, we declare `private final UserRepository userRepository;` and Spring creates one instance and injects it via the constructor. This is called the IoC (Inversion of Control) container.

**Q: What is the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`?**

A: All four are specializations of `@Component` — they all register the class as a Spring bean. The difference is semantic:
- `@Component` — generic component
- `@Service` — marks business logic layer
- `@Repository` — marks data access layer; also adds exception translation (converts database exceptions to Spring exceptions)
- `@Controller` / `@RestController` — marks web layer; `@RestController` adds `@ResponseBody` to every method

### JPA & Hibernate Questions

**Q: What is the difference between JPA and Hibernate?**

A: JPA is a specification (a set of rules/interfaces defined by Jakarta). Hibernate is the most popular implementation of those rules. JPA defines what methods should exist (`EntityManager.persist()`, `EntityManager.find()`). Hibernate provides the actual working code. Spring Data JPA adds another layer on top that generates repository implementations automatically.

**Q: What is the N+1 query problem and how did you prevent it?**

A: The N+1 problem happens when you load a list of entities, and then for each entity, a separate query is made to load a relationship. For example, loading 10 users and then making 10 separate queries to load each user's applications = 11 queries (1 + 10). We prevent it by using `FetchType.LAZY` on relationships — the collection is not loaded until explicitly accessed. When we need the data together, we'd use `@EntityGraph` or JPQL join fetch queries.

**Q: What is `@MappedSuperclass` and why did you use it?**

A: `@MappedSuperclass` marks a class whose fields should be included in the tables of its subclasses, but the class itself has no table. We used it for `BaseEntity` which has `createdAt` and `updatedAt` fields that all entities need. This follows the DRY principle — define once, inherit everywhere.

**Q: Explain `CascadeType.ALL` and `orphanRemoval = true`.**

A: `CascadeType.ALL` means operations on the parent cascade to the children. If we delete a `User`, all their `JobApplication` records are also deleted automatically. `orphanRemoval = true` means if a `JobApplication` is removed from the `user.getJobApplications()` list (detached from the parent), it's automatically deleted from the database. Together they ensure no orphaned applications exist.

**Q: What is `FetchType.LAZY` vs `FetchType.EAGER`?**

A: `EAGER` loads the related data immediately when the parent is loaded. `LAZY` loads it only when you actually access the collection. `LAZY` is better for performance — you don't always need all the related data. `EAGER` can cause performance problems by loading too much data. In our project, `@ManyToOne` on `JobApplication.user` is LAZY — we don't always need the full User object when fetching applications.

### Spring Security & JWT Questions

**Q: How does JWT authentication work in your project?**

A: When a user logs in, `AuthServiceImpl` verifies their email and password. If correct, `JwtUtil.generateToken(email)` creates a signed JWT token containing the user's email as the subject, with a 24-hour expiry, signed with a secret key using HS256 algorithm. The client receives this token and sends it on every subsequent request in the `Authorization: Bearer <token>` header. Our `JwtAuthFilter` (a `OncePerRequestFilter`) intercepts every request, extracts the token, validates the signature and expiry, loads the user from the database, and sets an `Authentication` object in `SecurityContextHolder`. The controller then uses `@AuthenticationPrincipal` to access the current user without any query parameter.

**Q: What is the SecurityContextHolder?**

A: It's Spring Security's per-thread storage for the current authenticated user. When our `JwtAuthFilter` validates a token, it creates a `UsernamePasswordAuthenticationToken` and stores it in `SecurityContextHolder.getContext().setAuthentication()`. For the rest of the request lifecycle, any code can call `SecurityContextHolder.getContext().getAuthentication()` to know who is making the request. It's cleared after the request completes.

**Q: Why is JWT stateless? What are its advantages?**

A: JWT is stateless because the server doesn't store any session information. All the information needed to verify the user is inside the token itself. Advantages:
1. Scalability — any server instance can verify any token (no shared session store needed)
2. Performance — no database lookup needed just to verify identity (the signature verification is in-memory)
3. Mobile-friendly — tokens work well with mobile apps and SPAs that don't use cookies

**Q: What is BCrypt and why use it for passwords?**

A: BCrypt is a password hashing function designed to be slow on purpose. When you register, your password is run through BCrypt which produces a 60-character hash like `$2a$10$...`. This hash includes a random "salt" (random value added before hashing). You can never reverse a BCrypt hash to get the original password. When logging in, BCrypt hashes the input password with the same salt and compares the result. The intentional slowness makes brute-force attacks impractical.

**Q: What is CSRF and why did you disable it?**

A: CSRF (Cross-Site Request Forgery) is an attack where a malicious website tricks a browser into making a request to your API using the user's existing session cookie. Spring Security adds CSRF tokens to prevent this. However, our API uses JWT tokens in the `Authorization` header — not cookies. Browsers don't automatically send headers to other sites, so CSRF attacks can't work. Disabling CSRF is safe and correct for stateless JWT APIs.

**Q: What is IDOR and how did you prevent it?**

A: IDOR (Insecure Direct Object Reference) is when a user can access another user's data by guessing an ID. For example, if I'm user 1 and I send `GET /api/applications/50`, and application 50 belongs to user 2, I should be denied. We prevent this at the repository level using `findByIdAndUserId(id, userId)`. The query is `WHERE id = ? AND user_id = ?`. Even if someone guesses another application's ID, they get a 404 because the user_id doesn't match.

### Docker Questions

**Q: What is Docker and why did you use it?**

A: Docker is a containerization platform. A container is an isolated, self-contained environment that packages your application with all its dependencies. I used Docker so that anyone can run this project with a single command — `docker compose up --build` — without installing Java, PostgreSQL, or configuring anything. The same containers run identically on any machine.

**Q: What is Docker Compose?**

A: Docker Compose is a tool for defining and running multi-container applications. Our `docker-compose.yml` defines two services — `postgres` (the database) and `app` (the Spring Boot application). With one command, both containers start, Docker creates a network between them, and they can communicate using service names.

**Q: How does the app container connect to the PostgreSQL container?**

A: Docker Compose creates an internal network and gives each service a DNS name matching its service name. So our app connects to `jdbc:postgresql://postgres:5432/trackmyjob_db` — `postgres` is the service name, not `localhost`. Docker's DNS resolves `postgres` to the database container's IP address automatically.

**Q: What is a Docker volume?**

A: Containers are ephemeral — when a container is removed, all its data is lost. A volume is storage that exists outside the container on the host machine. We use `postgres_data:/var/lib/postgresql/data` to store PostgreSQL data in a named volume. Even if we run `docker compose down` and restart, the database data persists.

**Q: Explain the two-stage Docker build.**

A: Stage 1 uses a full JDK image to compile the source code and build the JAR file. Stage 2 starts fresh with a minimal JRE image and copies only the JAR from Stage 1. The result is a small final image (~200MB) because we don't include the JDK compiler tools that are only needed during build. This is called a multi-stage build.

### Architecture & Design Questions

**Q: Explain the layered architecture of your project.**

A: The project follows a three-layer architecture. The Controller layer receives HTTP requests and sends responses — it has no business logic. The Service layer contains all business logic — it decides what to do. The Repository layer talks to the database using Spring Data JPA — it handles persistence. Each layer only talks to the layer directly below it. This separation means if I change the database, I only change the Repository. If I change business rules, I only change the Service. Controllers don't need to change.

**Q: Why use interfaces for services (AuthService, JobApplicationService)?**

A: Interfaces define a contract. Controllers depend on the `AuthService` interface, not the `AuthServiceImpl` concrete class. This follows the Dependency Inversion Principle (D in SOLID). In unit tests, we can mock the interface and inject the mock — the controller doesn't care if it's talking to the real service or a test double. Also, if we later need a different implementation, we add it without changing anything else.

**Q: What is the `@Transactional` annotation and when should you use it?**

A: `@Transactional` wraps a method in a database transaction. If any exception occurs inside the method, all database changes made within that transaction are rolled back. We add it to service methods because a single business operation might involve multiple database calls. For example, creating a job application involves finding the user (one query) and saving the application (another query). Without `@Transactional`, if an error occurs after the user is found but before the application is saved, you might have partial state. With `@Transactional`, either both succeed or both are rolled back.

**Q: What is the difference between `@NotNull` and `@NotBlank`?**

A: `@NotNull` only checks that the value is not null. An empty string `""` would pass `@NotNull`. `@NotBlank` checks that the value is not null, not empty, and not just whitespace. `"   "` would fail `@NotBlank`. For `String` fields in request bodies, always use `@NotBlank`. Use `@NotNull` for non-String types like `LocalDate` or `Long`.

### Testing Questions

**Q: What is the difference between unit tests and integration tests?**

A: Unit tests test a single class in isolation. All dependencies are replaced with mocks (fake objects). They run fast (milliseconds) and pinpoint exactly which business logic is broken. Integration tests test multiple components working together — usually the full HTTP stack. They're slower but verify that the layers integrate correctly. We use both: unit tests for service logic, integration tests for controller + security + validation behavior.

**Q: What is Mockito and how did you use it?**

A: Mockito is a Java mocking framework. In unit tests, we used `@Mock` to create fake versions of `UserRepository`, `PasswordEncoder`, and `JwtUtil`. We used `when(userRepository.existsByEmail(any())).thenReturn(true)` to define what the fake returns. This let us test `AuthServiceImpl`'s logic without a real database.

**Q: Why do integration tests use H2 instead of PostgreSQL?**

A: Running tests against a real PostgreSQL database requires the database to be running, which makes tests environment-dependent and harder to run in CI. H2 is an in-memory database that starts and stops with the tests — no external setup needed. We use `MODE=PostgreSQL` so H2 behaves like PostgreSQL (same SQL syntax). Tests run faster and work identically in any environment.

**Q: What is `@SpringBootTest`?**

A: It loads the complete Spring application context for integration testing. All beans are created, all auto-configuration runs, security is active. This gives us the most realistic test environment short of running the actual server.

**Q: What is MockMvc?**

A: MockMvc lets you send HTTP requests in tests without starting a real HTTP server. It goes through the full Spring MVC stack — DispatcherServlet, security filters, controllers, exception handlers — but faster than a real server. We use it to verify HTTP status codes, response body content (with JSONPath), and headers.

### CI/CD Questions

**Q: What does your GitHub Actions pipeline do?**

A: On every push to the `main` branch, GitHub Actions starts a fresh Ubuntu virtual machine, installs Java 21 (Temurin distribution), checks out the code, and runs `mvn clean verify`. This compiles the code, runs all unit tests and integration tests, and builds the JAR. If any test fails, the pipeline turns red and we know immediately. This prevents broken code from staying in the main branch undetected.

**Q: What is `mvn clean verify` vs `mvn test`?**

A: `mvn test` runs unit tests only. `mvn verify` runs unit tests AND integration tests AND package verification. `mvn clean` deletes the previous build output first, ensuring a completely fresh build. In CI we always use `mvn clean verify` to catch everything.

---

*This documentation was written to help you understand every line of the TrackMyJob project and prepare confidently for technical interviews. Every concept is explained the way it would be in a real team — with the reasoning behind each decision, not just the implementation.*

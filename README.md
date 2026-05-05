

# Rutina Auth Service

**Rutina Auth Service** — микросервис аутентификации и управления пользователями. Отвечает за регистрацию, вход, валидацию JWT токенов, управление профилями и ролевую модель доступа.

Сервис является частью микросервисной системы **Rutina** и взаимодействует с:
- **Main Service** — для обновления счётчиков привычек и очков (через Feign)
- **Gateway Service** — единая точка входа для клиентов
- **Android приложение** — мобильный клиент



---

## Что делает сервис

### Основные функции

| Функция | Endpoint | Метод | Доступ |
|---------|----------|-------|--------|
| Регистрация | `/auth/register` | POST | Все |
| Вход | `/auth/login` | POST | Все |
| Валидация токена | `/auth/validate` | GET | Авторизованные |
| Получение пользователя по ID | `/auth/user/{id}` | GET | Авторизованные |
| Увеличение счётчика привычек | `/auth/user/{id}/increment-habits` | POST | Main Service |
| Уменьшение счётчика привычек | `/auth/user/{id}/decrement-habits` | POST | Main Service |
| Начисление очков | `/auth/user/{id}/add-score` | POST | Main Service |
| Список всех пользователей | `/admin/allusers` | GET | ADMIN |
| Информация о пользователе | `/admin/info/{id}` | GET | ADMIN |
| Удаление пользователя | `/admin/delete/{id}` | DELETE | ADMIN |

### Бизнес-логика

1. **Регистрация**:
   - Проверяет уникальность email
   - Хеширует пароль через **BCrypt**
   - Создаёт пользователя с ролью `USER`
   - Генерирует **JWT токен** с `id`, `email`, `roles`
   - Срок действия токена: **24 часа**

2. **Вход**:
   - Аутентифицирует через `AuthenticationManager`
   - Генерирует новый JWT токен
   - Возвращает токен и username

3. **Валидация токена**:
   - Извлекает токен из заголовка `Authorization: Bearer {token}`
   - Проверяет подпись и срок действия
   - Возвращает `UserDto` с ролью

4. **Управление счётчиками** (для Main Service):
   - `increment-habits`: увеличивает `countOfHabits` (максимум 10)
   - `decrement-habits`: уменьшает `countOfHabits` (минимум 0)
   - `add-score`: начисляет очки за завершённые привычки

5. **Админ-панель**:
   - Просмотр всех пользователей
   - Просмотр конкретного пользователя
   - Удаление пользователей

---

## Технологический стек

- **Kotlin**
- **Spring Boot 3.5.7**
- **Spring Security** — аутентификация и авторизация
- **Spring Data JPA** (Hibernate) — работа с PostgreSQL
- **JJWT** (io.jsonwebtoken) — генерация и валидация JWT
- **BCrypt** — хеширование паролей
- **Spring AOP** — глобальная обработка исключений
- **PostgreSQL** — база данных пользователей
- **Gradle** — сборка проекта

---

## Структура проекта

```
src/main/kotlin/com/example/demo/
├── config/
│   └── SecurityConfig.kt              # Конфигурация Spring Security
├── controller/
│   ├── AuthController.kt              # Контроллер аутентификации
│   └── AdminController.kt             # Контроллер админ-панели
├── database/
│   ├── dao/
│   │   └── UserDao.kt                 # Репозиторий пользователей
│   └── entity/
│       ├── AbstractEntity.kt          # Базовый класс сущности
│       └── Users.kt                   # Сущность пользователя
├── exceptions/
│   ├── CustomAccessDeniedHandler.kt   # Обработчик 403 Forbidden
│   ├── CastomExeptions.kt             # Кастомные исключения
│   ├── ExeptionDto.kt                 # DTO ошибки
│   └── GlobalExeptions.kt             # Глобальный обработчик исключений
├── model/
│   ├── Role.kt                        # Enum ролей (USER, ADMIN)
│   ├── dto/
│   │   ├── rq/
│   │   │   └── CreateUserRq.kt        # DTO запроса регистрации
│   │   └── rs/
│   │       └── UserDto.kt             # DTO пользователя
│   └── mapper/
│       └── UserMapper.kt              # Маппер сущность ↔ DTO
├── security/
│   ├── JwtAuthenticationEntryPoint.kt # Точка входа для 401 ошибок
│   ├── JwtRequestFilter.kt            # Фильтр JWT запросов
│   ├── JwtTokenProvider.kt            # Генерация и валидация JWT
│   └── UserPrincipal.kt              # UserDetails реализация
├── service/
│   ├── AdminService.kt                # Интерфейс сервиса админа
│   ├── UserService.kt                 # Интерфейс сервиса пользователей
│   └── impl/
│       ├── AdminServiceImpl.kt        # Логика админ-панели
│       ├── UserDetailsServiceImpl.kt  # Загрузка пользователя для Security
│       └── UserServiceImpl.kt         # Логика пользователей
└── DemoApplication.kt                 # Точка входа + инициализация админа
```

---

## База данных

### Таблица `users`

| Колонка | Тип | Описание |
|---------|-----|----------|
| `id` | BIGINT PK | Уникальный идентификатор |
| `name` | VARCHAR | Имя пользователя |
| `email` | VARCHAR UNIQUE | Email (логин) |
| `phone` | VARCHAR | Телефон |
| `password` | VARCHAR | Хеш пароля (BCrypt) |
| `balance` | INT | Баланс |
| `total_score` | INT | Всего очков |
| `count_of_habits` | INT | Счётчик активных привычек |
| `role` | VARCHAR | Роль: USER или ADMIN |
| `created_at` | TIMESTAMP | Дата регистрации |

### Инициализация админа

При первом запуске автоматически создаётся администратор:

```kotlin
@Bean
fun initAdmin(userDao: UserDao, passwordEncoder: PasswordEncoder): CommandLineRunner {
    return CommandLineRunner {
        if (!userDao.existsByEmail("admin@example.com")) {
            val admin = Users(
                name = "System Administrator",
                email = "admin@example.com",
                phone = "+79991112233",
                password = passwordEncoder.encode("Admin123!"),
                role = Role.ADMIN
            )
            userDao.save(admin)
        }
    }
}
```

**Учётные данные по умолчанию:**

| Роль | Email | Пароль |
|------|-------|--------|
| ADMIN | `admin@example.com` | `Admin123!` |

---

## API

### `POST /auth/register`

Регистрация нового пользователя.

**Тело запроса:**
```json
{
  "name": "Иван Петров",
  "username": "ivan@example.com",
  "password": "securePassword123",
  "phone": "+79991234567"
}
```

**Ответ (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "ivan@example.com"
}
```

**Ошибки:**
- `400` — пользователь с таким email уже существует
- `400` — ошибка валидации полей

### `POST /auth/login`

Вход в систему.

**Тело запроса:**
```json
{
  "username": "admin@example.com",
  "password": "Admin123!"
}
```

**Ответ (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin@example.com"
}
```

**Ошибки:**
- `401` — неверный email или пароль

### `GET /auth/validate`

Валидация токена и получение профиля.

**Заголовок:** `Authorization: Bearer {jwt_token}`

**Ответ (200):**
```json
{
  "id": 1,
  "name": "System Administrator",
  "email": "admin@example.com",
  "phone": "+79991112233",
  "balance": 0,
  "totalScore": 0,
  "countOfHabits": 0,
  "role": "ADMIN",
  "createdAt": "2026-05-01T10:00:00"
}
```

### `GET /auth/user/{id}`

Получение пользователя по ID.

### `POST /auth/user/{id}/increment-habits`

Увеличение счётчика привычек (вызывается Main Service).

**Лимит:** максимум 10 активных привычек.

### `POST /auth/user/{id}/decrement-habits`

Уменьшение счётчика привычек (минимум 0).

### `POST /auth/user/{id}/add-score`

Начисление очков пользователю.

**Параметры:** `points` — количество очков.

### `GET /admin/allusers`

Список всех пользователей (только ADMIN).

**Заголовок:** `Authorization: Bearer {admin_jwt_token}`

### `DELETE /admin/delete/{id}`

Удаление пользователя (только ADMIN).

---

## Безопасность

### JWT токен

```kotlin
private const val JWT_SECRET = "delivery-kfd-secret-key-change-in-prod-32chars"
private const val JWT_EXPIRATION = 86400000L  // 24 часа
```

Токен содержит:
- `sub` — email пользователя
- `id` — ID пользователя
- `roles` — список ролей (ROLE_USER, ROLE_ADMIN)
- `iat` — время создания
- `exp` — время истечения

### Spring Security

```kotlin
http
    .csrf { it.disable() }
    .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    .authorizeHttpRequests {
        it.requestMatchers("/admin/**").hasRole("ADMIN")
        it.requestMatchers("/auth/**", "/error", "/swagger-ui/**").permitAll()
        it.anyRequest().authenticated()
    }
    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
```

- **Stateless** сессии (без кук)
- **JWT фильтр** проверяет каждый запрос
- **Role-based access:** `/admin/**` только для ADMIN
- **CORS** разрешён для всех источников

### Роли

| Роль | Права |
|------|-------|
| `USER` | Создание/просмотр/удаление своих привычек, просмотр профиля |
| `ADMIN` | Всё выше + просмотр всех пользователей, удаление пользователей |

### Глобальный обработчик ошибок

```kotlin
@ControllerAdvice
class GlobalExceptionHandler {
    // 400 — UserExistException, HabitLimitExceededException
    // 401 — UsernameNotFoundException, InvalidTokenException
    // 403 — AccessDeniedException (CustomAccessDeniedHandler)
    // 404 — UserDoesntExistException
    // 500 — Exception
}
```

Все ошибки возвращаются в формате:
```json
{
  "status": 401,
  "error": "Ошибка входа",
  "message": "Неверный email или пароль",
  "timestamp": "2026-05-01T10:00:00"
}
```

---

## Конфигурация

### `application.yaml`

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: create-drop  # Автосоздание таблиц
    show-sql: true
```


---

## Зависимости проекта

| Сервис | Порт | Репозиторий |
|--------|------|-------------|
| Gateway Service | 8080 | [Rutina Gateway](https://github.com/ladatkoS/Rutina_Gateway_Service.git) |
| **Auth Service** | **8082** | **этот репозиторий** |
| Main Service | 8083 | [Rutina Main](https://github.com/ladatkoS/Rutina_Main_Service.git) |
| Android App | — | [Rutina Android](https://github.com/ladatkoS/Rutina_Android.git) |
| Neural Network | 8000 | [Rutina NN](https://github.com/AntonSlon/Rutina-neural-network.git) |

---

## Автор

**Савелий Ладатко**
- GitHub: [@ladatkoS](https://github.com/ladatkoS)
- Проект: [Rutina Auth Service](https://github.com/ladatkoS/Rutina_Auth_Service)

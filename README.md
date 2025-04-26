# Online Bus Ticket Booking System
A full-stack Online Bus Ticket Reservation application built with Spring Boot, Spring Security, JWT, Thymeleaf, and MySQL.

# 1. Introduction
Bus Ticket Reservation Application built using Spring Boot, Spring Security, Thymeleaf, JWT Authentication, and MySQL Database.
It allows users to search buses, book/cancel seats, view bookings, and provide feedback, while Admins can manage buses and schedules.

# 2. System Architecture
Technologies Used:

* Backend: Spring Boot, Spring Security, JWT, JPA
* Frontend: Thymeleaf, HTML, CSS, Bootstrap
* Database: MySQL
* Security: JWT Authentication with Spring Security
* Testing: Mockito, Spring Boot Test

# 3. Features
* User Authentication & Role Management
a) User registration and login using JWT Token stored in HTTP-only cookies.
b) Role-based access control: Admin and User roles.
c) Passwords are encrypted using BCrypt.

* Admin Features
a) Admin dashboard for managing buses and schedules.
b) Add new buses with multiple schedules.
c) Update existing bus schedules.
d) Delete buses along with their schedules.

* User Features
a) Search buses by source, destination, and date.
b) View available buses and available seats.
c) Select seats and book tickets.
d) View and cancel booked reservations.
e) Submit feedback.

* Booking Management
a) Only available seats are shown during seat selection.
b) Prevent double booking of seats.
c) Manage available seats dynamically.

* Feedback System
Users can submit feedback with name, email, and message.

# 4. Security Configuration
* SecurityConfig.java
a) JWT Authentication filter applied before UsernamePasswordAuthenticationFilter.
b) Stateless session management.
c) Authorization rules:
d) /auth/**, /, /css/**, /js/**, /images/** → Public access
e) /admin/** → Admin only
f) All other requests → Authenticated users
g) Passwords encrypted with BCryptPasswordEncoder.

# 5. Controllers
* AuthController
a) /auth/registerForm → Show registration page
b) /auth/loginForm → Show login page
c) /auth/authenticate → Authenticate user and issue JWT token
d) /auth/change-password → Allow users to change password

* AdminController
a) /admin/dashboard → Admin home page
b) /admin/add → Add new bus
c) /admin/schedules/editExisting/{busId} → Edit existing schedules
e) /admin/schedules/add/{busId} → Add new schedules
f) /admin/buses/delete/{busId} → Delete bus

* BusController
a) /buses/viewAllBuses → View all buses
b) /buses/viewBuses → View buses by bus type
c) /buses/{busId} → View bus details and seat selection page

* ReservationController
a) /reservation/buslist → List buses matching source, destination, and date
b) /reservation/seats → Show seat selection for a bus
c) /reservation/confirm → Book selected seats
d) /reservation/reservations → View user's booked reservations
e) /reservation/cancel → Cancel a booked reservation

* FeedbackController
a) /feedback/form → Submit feedback form
b) /feedback/submit → Save feedback

* HomeController
/, /dashboard, /about, /service, /contact → Static pages

# 6. Service Layer
* BusService: Bus management (CRUD operations)
* ScheduleService: Manage bus schedules
* ReservationService: Seat booking and reservation management
* FeedbackService: Submit and manage feedback

# 7. Repository Layer
* UserRepository: Manage users
* BusRepository: Manage buses
* ScheduleRepository: Manage schedules
* ReservationRepository: Manage reservations
* FeedbackRepository: Manage user feedback

# 8. Entities (Database Models)
* User: Stores username, email, encrypted password, role.
* Bus: Contains bus details (name, type, seats, registration number).
* Schedule: Bus schedule (source, destination, timings, price).
* Reservation: Stores booked seats and booking timestamps.
* Feedback: Stores user feedback.
* Role: Enum (USER, ADMIN).

# 9. Exception Handling
Custom exceptions like:
* UserNotFoundException
* BusNotFoundException
* SeatAlreadyBookedException
* ReservationNotFoundException
* ScheduleNotFoundException

Centralized exception handling with proper error messages.

# 10. Templates (Thymeleaf Views)
* register.html → User registration
* login.html → User login
* adminlogin.html → Admin login
* dashboard.html → User dashboard
* frontpage.html → Landing page
* busList.html → View buses
* userBusList.html → View buses
* seatSelection.html → Select seats
* userReservations.html → View user bookings
* feedback.html → Submit feedback
* admindashboard.html → Admin dashboard
* addbus.html → Add new bus
* editExistingSchedules.html → Update schedules
* addSchedules.html → Add new schedules
* viewSchedules.html → View bus schedules
* password.html -> change password
* about.html
* service.html
* contact.html

# 11. How to Run the Project
* Configure MySQL database in application.properties.
* Added required dependencies from pom.xml.
* Run the Spring Boot application.

Accessed at:
User → http://localhost:8080/
Admin → http://localhost:8080/admin/login

# 12. Testing Overview
Frameworks:
* JUnit 5
* Mockito
* Spring Boot Test

# Techniques:
* Unit Testing for Services
* MockMvc Testing for Controllers
* JWT-based security tests
* Validation and input constraint tests

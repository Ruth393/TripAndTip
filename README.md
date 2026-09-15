# TripAndTip

A full-stack travel planning web application built with Angular and Spring Boot.

TripAndTip allows users to create and manage trips, explore destinations, interact with other users, and use an AI-powered assistant for trip planning.

## Features

* User registration and authentication
* Google OAuth2 authentication
* User profiles
* Create and manage trips
* Destination search and map integration
* Comments and user interactions
* Follow users
* AI travel assistant powered by Google Gemini
* AI-generated packing lists
* Image support
* Protected API endpoints
* Persistent data storage with JPA/Hibernate

## Tech Stack

### Frontend

* Angular
* TypeScript
* HTML
* CSS

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST API
* Maven

### Database

* H2
* JPA / Hibernate

### Authentication

* JWT
* OAuth2
* Google Login

### AI

* Google Gemini
* Spring AI

### Tools

* Git
* GitHub
* IntelliJ IDEA
* VS Code

## Architecture

```text
┌───────────────────────┐
│       Angular         │
│       Frontend        │
└───────────┬───────────┘
            │
            │ REST API
            ▼
┌───────────────────────┐
│      Spring Boot      │
│       Backend         │
│                       │
│ Controllers           │
│ Services              │
│ Repositories          │
│ Security              │
│ AI Integration        │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│      H2 Database      │
│    JPA / Hibernate    │
└───────────────────────┘
```

The Angular frontend communicates with the Spring Boot backend through REST APIs.

The backend handles business logic, authentication, database access, and AI integration.

## AI Integration

TripAndTip integrates Google Gemini through Spring AI.

The AI assistant is implemented on the backend and exposed through REST endpoints.

It can assist with:

* Trip planning
* Destination-related questions
* Contextual travel assistance
* Packing list generation

The general request flow is:

```text
Angular
   |
   v
REST API
   |
   v
Spring Boot
   |
   v
AI Service
   |
   v
Spring AI / Gemini
   |
   v
Response
```

## Authentication

The application uses Spring Security to protect backend resources.

Authentication includes:

* JWT-based authentication
* Google OAuth2 login
* Protected REST endpoints
* Authenticated user context

Sensitive credentials are stored using environment variables and are not committed to the repository.

## Project Structure

```text
Trip/
│
├── trip-client/              # Angular frontend
│   └── src/
│       └── app/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── trip/
│       │               ├── controller/
│       │               ├── service/
│       │               ├── repository/
│       │               ├── model/
│       │               └── security/
│       │
│       └── resources/
│           └── application.properties
│
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

* Java 17+
* Node.js
* Angular CLI
* Git

### Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
cd Trip
```

### Configure Environment Variables

The application requires environment variables for external services and authentication.

Example:

```text
GEMINI_KEY=your_gemini_api_key
JWT_SECRET=your_jwt_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_app_password
```

Do not commit real API keys, passwords, client secrets, or JWT secrets to GitHub.

## Run the Backend

From the project root:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

## Run the Frontend

Open a second terminal:

```bash
cd trip-client
```

Install dependencies:

```bash
npm install
```

Run Angular:

```bash
ng serve
```

The frontend will be available at:

```text
http://localhost:4200
```

## Database

The application currently uses H2 for development.

JPA/Hibernate is responsible for mapping Java entities to database tables.

The database schema is configured to update automatically during development.

## API

The frontend communicates with the backend through REST APIs.

Examples of application endpoints include:

```text
/api/trip/chat
/api/trip/packingList/{id}
```

Additional endpoints handle authentication, users, trips, destinations, comments, and other application functionality.

## Screenshots

### Home

*Add screenshot here*

### Trip Planning

*Add screenshot here*

### Map

*Add screenshot here*

### AI Assistant

*Add screenshot here*

### User Profile

*Add screenshot here*

## What I Worked On

The project provided hands-on experience in developing a complete full-stack application.

Key areas include:

* Designing and implementing REST APIs
* Developing Angular components and services
* Designing JPA entities and database relationships
* Implementing authentication and authorization
* Integrating Google OAuth2
* Integrating Google Gemini through Spring AI
* Connecting frontend and backend layers
* Debugging issues across multiple application layers
* Managing configuration and environment variables

## Future Improvements

* Weather-based recommendations
* Enhanced route planning
* Improved responsive and mobile experience
* Additional notification features
* Expanded automated testing
* Production deployment

## Author

**Ruth Barzilay**

Full-Stack Developer

GitHub: [Ruth393](https://github.com/Ruth393)

## License

This project was created as a personal development and portfolio project.

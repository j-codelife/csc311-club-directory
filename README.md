# Campus Club Directory

This is a Spring Boot application I created for my CSC 311 Advanced Programming class. It keeps track of campus clubs and allows users to view and add clubs through a simple webpage.

## Technologies Used
- Java 25
- Spring Boot 4.1.1
- Maven
- Apache Derby
- HTML, CSS, and JavaScript

## Features
- View a list of campus clubs
- Add new clubs using a form
- Validate required fields
- Search clubs by category through the REST API
- Save club information in an Apache Derby database

## How to Run
1. Open the project in IntelliJ IDEA.
2. Make sure JDK 25 is selected.
3. Reload the Maven project.
4. Run `ClubdirectoryApplication.java`.
5. Open `http://localhost:8085/` in your browser.

## REST API
- `GET /api/clubs` — View all clubs
- `GET /api/clubs?category=Sports` — Filter clubs by category
- `GET /api/clubs/{id}` — Find a club by ID
- `POST /api/clubs` — Add a new club

The application starts with three clubs: Computer Science Club, Chess Club, and Running Club. New clubs are saved in the Derby database.

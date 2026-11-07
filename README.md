# Campus Club Directory

A CSC 311 practice assignment for viewing campus clubs and adding a club through
a simple HTML form. Clubs are stored in a local Apache Derby database.

## Technologies

- Java 25 and Maven (wrapper included)
- Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, and Jakarta Validation
- Apache Derby and Hibernate Community Dialects (versions managed by Spring Boot)
- Plain HTML, CSS, and JavaScript

## Run in IntelliJ IDEA

1. Open this project and reload Maven using **Reload All Maven Projects**.
2. Set the project SDK and Maven runner JRE to **JDK 25**.
3. Open `src/main/java/edu/farmingdale/csc311/clubdirectory/ClubdirectoryApplication.java`.
4. Run `ClubdirectoryApplication.main()`. Use the project root as the working
   directory so Derby saves to the correct local `data` folder.
5. Open [http://localhost:8085/](http://localhost:8085/).

If IntelliJ still shows the old generated `Csc311Module8Application` run
configuration, run the new main class listed above instead.
Only run one application instance at a time.

## Run from a terminal

With JDK 25 selected by `JAVA_HOME`, run these commands from the project root:

```sh
./mvnw clean verify
./mvnw spring-boot:run
```

On this Mac, select the installed JDK first:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
```

On Windows, use `mvnw.cmd` instead of `./mvnw` and select JDK 25.

## API

| Request | Result |
| --- | --- |
| `GET /api/clubs` | All clubs, HTTP 200 |
| `GET /api/clubs?category=Sports` | Case-insensitive category search, HTTP 200 |
| `GET /api/clubs/{id}` | One club, HTTP 200; missing club, HTTP 404 |
| `POST /api/clubs` | Saved club with generated ID, HTTP 201; invalid input, HTTP 400 |

Example POST body:

```json
{
  "name": "Robotics Club",
  "category": "Academic",
  "meetingDay": "Thursday",
  "room": "Lupton Hall 210"
}
```

Name, category, and meeting day are required. Maximum lengths are 60, 30, 20,
and 40 characters for name, category, meeting day, and optional room respectively.

## Code overview

- `Club` holds a club's fields and validation rules and maps to a database table.
- `ClubRepository` provides database operations and category searching.
- `ClubController` handles the three API operations.
- `DataLoader` adds Computer Science Club, Chess Club, and Running Club only
  when the database table is empty.
- `index.html` loads the API data and submits the Add a club form.

## Database and testing

The database is saved at `data/clubs-db`. Restarting the application preserves
saved clubs and does not duplicate the starter clubs. `data/`, `derby.log`, build
outputs, and local IDE settings are ignored by Git. Do not delete the database
to reset a demonstration.

`./mvnw clean verify` runs seven focused integration tests using a separate
in-memory Derby database. Test changes are rolled back and do not affect saved
local clubs. Tests cover starter data, category filtering, lookup and 404,
creation, required fields, maximum lengths, and optional room.

Live checks also verified the browser form, red validation message, HTTP
responses, and persistence across an application restart. This machine's saved
database includes Photography Club (browser test) and Robotics Club (API test),
in addition to the three starter clubs. These local records are not uploaded;
a fresh clone starts with the three starter clubs.

The required Derby/Hibernate versions can log nonfatal JDBC capability warnings
during startup, including an unsupported `createNClob` probe. Startup, database
operations, and all tests still succeed with the assignment's exact YAML.

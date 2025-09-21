# YouTube Backend Assignment

The original assignment was to be done in Django/Python, but I have implemented it using Java and Spring Boot, as our course was also focused on Java. I am not familiar with Django yet but I plan to learn it in the future.

## What it does
- Fetches the latest YouTube videos for a given search term (currently set to `chess` in the code).
- A background job runs every 10 seconds to fetch new videos from YouTube.
- Stores video details like title, description, published date, and thumbnails in an H2 database.
- Provides REST APIs to list and search the stored videos with pagination.

## How to run
1. Clone this repo and open it in IntelliJ IDEA.
2. Go to `src/main/resources/application.properties` and add your YouTube API key:
3. Run the application from `YouTubeBackendApplication.java`.
4. Test APIs in browser or Postman:
- List videos → `http://localhost:8080/videos?page=0&size=10`
- Search videos → `http://localhost:8080/videos/search?query=chess&page=0&size=10`
5. (Optional) Open H2 console to see the database:
- URL → `http://localhost:8080/h2-console`
- JDBC URL → `jdbc:h2:mem:testdb`
- Username → `sa`
- Password → *(leave blank)*

## Tech stack
- Java 17  
- Spring Boot  
- Spring Data JPA  
- H2 Database  
- YouTube Data API v3

## Notes
- The search term is hardcoded in the service class right now. This can be moved to `application.properties` later for easier config.
- Future improvement: add a small dashboard to view videos in the browser.

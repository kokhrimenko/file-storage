File storage
------------

Implement a "file storage" REST web-service.

Minimum functionality:
* Upload file (must return file ID) and store it in file system
* Serve file. User can download file by ID
* Get a list of files available for download (name + ID)

Optional functionality:
* Create new user: login/password
* Files uploaded by specific user are available only to him
* Get a list of files available for a specific user
* Share file with a specific user: 

You are free to choose any tools/libraries you like.

For example you can use: 
* Spring Boot: https://spring.io/projects/spring-boot
* Hibernate ORM: http://hibernate.org
* Spring-Swagger integration for REST: http://springfox.github.io/springfox

Dependencies must be managed with a package manager (Maven, Gradle etc).

Writing any test for any part of the program is not a must, 
but is definitely a bonus.

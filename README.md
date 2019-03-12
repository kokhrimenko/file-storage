BRIEF DESCRIPTION:
- I've preserved the task requirement at the bottom of this documents.
- I didn't use swagger because this service is pretty simple and, as for me doesn't required it.
- I've little simplified StorageService because I really don't need some of them and decided to remove their for economy time reason.
- I didn't implement share to user and user registration features.
- I use simple Front-End forms and scripts to minimize time consumption.

Installation instruction:

1 You should configure file_store.path.root (last line) into the application.yml file. This path onto the FS, where you planning to store uploaded file (this folder should have appropriate permissions).
2 For run application please use "run.sh" script (It works only in Linux bases scripts, for start on Windows you can use command from this script directly)
3 System have 2 predefined users:
	username: user1
	pass: user_1
and
	username: user2
	pass: user_2
password without encryption. You can change this data, and add more users into file "data.sql"
4 I've used in-memory DB, so every restarts - user contents (files) will be loosed


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

# GuessTheArtist
Client ID: 45826b5df24a4a208d482e12c8dd400b

Instructions on compiling and running the program:
1. With Docker running, open a terminal window:
	cd GuessTheArtist/server

2. Create the server docker image:
	docker image build -t server .

3. Create and run the docker container from the image:
	"docker container run --rm -it --name server -p 1337:1337 server"

4. Open a new terminal:
	docker network ls
	docker network inspect bridge

   Under containers, find the IP address of the running server container.

5. Edit MtClient.java and change the hostname to your specific IP address.

6. Compile all java files
	javac *.java
   Note: if your jdk is more recent than version 8, you may need to use:
	javac -source 1.8 -target 1.8 *.java

7. Move the MtClient.class file into the client subdirectory.

8. Open another terminal:
	cd GuessTheArtist/client

9. Create the client docker image:
	docker image build -t client .

10. Create and run the docker container from the image:
	docker container run --rm -it --name red client

11. Open another terminal:
	cd GuessTheArtist/client
	docker container run --rm -it --name host client

To quit, control-c in each terminal window that has a running container.

Follow the on screen instructions and enjoy!

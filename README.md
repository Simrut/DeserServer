# Basic Java Web Server

This project is a Java based multithreaded TCP web server that implements responses for HTTP 200, 301, and 404 codes.
It was made using Java 8 and runs by default on port 6789 of localhost.


## Vulnerability

The server has the vulnerable Apache Commons Collection 3.1 in its Classpath which can lead to code execution.
It is to be found in file DeserServer/src/Connection.java, where a base64 string gets decoded and deserialized, as well as cast to an object.

## Usage

- Compiling the Program

    - Open console and type **`javac src/WebServer.java`**

- Running the Program

    - Type **`java src/WebServer`** to run the program or use your preferred IDE

- Testing HTTP 200 OK:

    - In your browser of choice type **`localhost:6789`** into the address bar.
    A basic webpage should appear.

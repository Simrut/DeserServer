package src;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import org.apache.commons.collections.*;
import javax.management.BadAttributeValueExpException;

class InfectionDatabase implements Serializable {
    private String databaseContent;
    }


/**
 * Connection Object.
 * <p>
 * Handles requests made by a client socket on behalf of the server by
 * sending appropriate response. Implements the Runnable interface to
 * allows the Connection object to run inside of a thread.
 *
 * @author Maurice Harris 1000882916
 */
public class Connection implements Runnable {

    // The socket used to connect to the server
    private Socket connectionSocket;
    // A HashMap to store the keys/values inside of the client request
    private Object objectToDeserialize;


    /**
     * Creates a Connection object
     *
     * @param connectionSocket The socket the client used to connect to the server
     */
    public Connection(Socket connectionSocket) {

        this.connectionSocket = connectionSocket;
    }

    /**
     * Parses the client request and inserts all the request fields into the
     * request HashMap. The key is the request field while the value is the value
     * of the field.
     *
     * @throws IOException if the connectionSocket is not present and the its inputStream is inaccessible
     */
    private void parseRequest() throws IOException {//TODO implement the vuln here

        // Connect a BufferedReader to the client socket's input stream and read it its request data
        BufferedReader connectionReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        //code to read and print headers
        String headerLine = null;
        if ((headerLine = connectionReader.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

//code to read the post payload data
        StringBuilder payload = new StringBuilder();
        while (connectionReader.ready()) {
            payload.append((char) connectionReader.read());
        }
        System.out.println("Payload data is: " + payload.toString());

        String objectString = payload.toString().split("\n")[7]; //TODO find a better way to split from header
       /* InputStream is = connectionSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);*/
        try {
            byte [] data = Base64.getDecoder().decode( objectString );
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(  data ) );
            BeanMap object  = (BeanMap) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the appropriate response based on the client request.
     * <p>
     * If the URL requested is inside of the redirect HashMap
     * the client is sent a HTTP 301 error redirect response, sending the client to
     * the new URL location. If the URL requested is not inside of the redirect HashMap
     * and does not exist an HTTP 404 error response is sent. If the URL request
     * exists an HTTP 200 OK response is sent.
     *
     * @throws IOException if outStream, fileStream, or bufInputStream is closed or does not exist
     *                     while they are being used.
     */
    private void sendResponse() throws IOException {

        // Create an DataOutputStream, outStream, to be able to send information out to the client connection
        DataOutputStream outStream = new DataOutputStream(connectionSocket.getOutputStream());

        // Create HTTP 200 response with a basic webpage
        String http200Response = "HTTP/1.1 200 OK\r\n\r\n" + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title>My Network Project</title>\n" +
                "</head>\n" +
                "\n" +
                "<body><h1>\n" +
                "This is your test page\n" +
                "</h1></body>\n" +
                "\n" +
                "</html>";

        // Send the HTTP 404 response to the client using the UTF-8 encoding
        outStream.write(http200Response.getBytes("UTF-8"));

        // Close the output stream
        outStream.close();
    }

    /**
     * Ran at the start of the runnable Connection object's execution inside of a thread
     */
    @Override
    public void run() {
        try {
            // Parse the client request and store the request field keys/values inside of the request HashMap
            parseRequest();

            // Send an appropriate response to the client based on the request received by the server
            sendResponse();

            // Close the client connection
            this.connectionSocket.close();
        } catch (IOException ex) {
            // If an IOException is caught print out the stack of commands that leads to the error
            ex.printStackTrace();
        }
    }
}

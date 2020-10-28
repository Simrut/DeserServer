package src;



import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

/**
 * WebServer Object.
 *
 * WebServer represents a server that serves up web content through its
 * ServerSocket. Listens indefinitely for new client connections and creates
 * a new thread to handle client requests.
 *
 * @author Maurice Harris 1000882916
 *
 */
public class WebServer {

    /**
     * Creates the ServerSocket and listens for client connections, creates a
     * separate thread to handle each client request.
     *
     * @param args an array of arguments to be used in the
     *
     *
     */
    public static void main(String[] args) throws Exception {
        String ksName = "vuln_serv_keystore.jks";
        char ksPass[] = "HerongJKS".toCharArray();
        char ctPass[] = "HerongJKS".toCharArray();

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(ksName), ksPass);
        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, ctPass);
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);
        SSLServerSocketFactory ssf = sc.getServerSocketFactory();

        // Create SSLServerSocket on LocalHost, port 6789
        SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(6789);
        System.out.println("Listening for HTTPS connections on port 6789...\r\n");

        // Listen for new client connections
        while(true) {

            // Accept new client connection
            Socket connectionSocket = serverSocket.accept();

            // Create new thread to handle client request
            Thread connectionThread = new Thread(new Connection(connectionSocket));

            // Start the connection thread
            connectionThread.start();
            System.out.println("New connection on port 6789...\r\n");
        }
    }
}

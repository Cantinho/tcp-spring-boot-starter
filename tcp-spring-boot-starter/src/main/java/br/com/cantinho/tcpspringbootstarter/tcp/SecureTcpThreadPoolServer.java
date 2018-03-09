package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static br.com.cantinho.tcpspringbootstarter.ApplicationConfig.SECURE_SERVER_IMPLEMENTATION;

/**
 * Listens for connections on a specified port.
 * Reads a line from the newly-connected socket.
 * Converts the line to uppercase and responds on the socket.
 * Repeats the previous step until the connection is closed by the client.
 */
@Component(value = SECURE_SERVER_IMPLEMENTATION)
public class SecureTcpThreadPoolServer extends Thread implements TcpServer, TcpConnection.Listener {

  /**
   * A logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SecureTcpThreadPoolServer.class.getCanonicalName());

  /**
   * Default server port.
   */
  private static final int DEFAULT_PORT = 8080;

  @Value( "${tcp.server.port}" )
  private int defaultPort;

  @Value( "${tcp.server.securePort}" )
  private int securePort;

  @Value( "${tcp.server.keystoreName}" )
  private String keystoreName;

  @Value( "${tcp.server.keystorePass}" )
  private String keystorePass;


  /**
   * Pool of worker threads of unbounded size. A new thread will be created
   * for each concurrent connection, and old threads will be shut down if they
   * remain unused for about 1 minute.
   */
  private final ExecutorService workers = Executors.newCachedThreadPool();

  /**
   * Server socket on which to accept incoming client connections.
   */
  private ServerSocket serverSocket;

  /**
   * Port on which server is running.
   */
  private Integer port;

  /**
   * Flag to keep this server running.
   */
  private volatile boolean keepRunning = true;

  /**
   * List of all TCP connections.
   */
  private List<TcpConnection> connections = new ArrayList<>();

  /**
   * List of all TCP connection listeners.
   */
  private List<TcpConnection.Listener> listeners = new ArrayList<>();

  /**
   * Creates a new threaded echo server on the {@port}. Throws exception if
   * it is unable to bind to the specified port {@port}.
   */
  public void init(final Integer port) {
    // Capture shutdown requests from the Virtual Machine.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        SecureTcpThreadPoolServer.this.shutdown();
      }
    });
    try {

      //this.serverSocket = new ServerSocket(port);
      this.serverSocket = getSSLServerSocket();
      LOGGER.info("Server start at port " + port);
    } catch (TcpException exc) {
      LOGGER.warn("ignored\n" + exc.getMessage());
      LOGGER.error("May be port " + port + " busy.");
    }
  }

  /**
   * Creates a server socket for secure communication using provided keystore and credentials.
   *
   * @return a secure server socket.
   * @throws Exception an Exception can be thrown if ssl server socket cannot be created.
   */
  private SSLServerSocket getSSLServerSocket() throws TcpException {
    try {
      final KeyStore keyStore = KeyStore.getInstance("PKCS12");
      final InputStream keystoreInputStream = new ClassPathResource(keystoreName).getInputStream();
      keyStore.load(keystoreInputStream, keystorePass.toCharArray());

      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, keystorePass.toCharArray());

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), null, null);
      final SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
      if(null != socketFactory) {
        return (SSLServerSocket) socketFactory.createServerSocket(securePort);
      }
    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
        KeyManagementException | IOException exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
      throw new TcpException("Could not establish secure communication.", exc);
    } catch (UnrecoverableKeyException exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
    }
    throw new TcpException("Could not establish secure communication.");
  }

  /**
   * This is executed when {@method TcpThreadPoolServer.start()} is invoked by another thread.
   * Will listen for incoming connections and hand them over to the ExecutorService (thread pool)
   * for the actual handling of client I/O.
   */
  @Override
  public void run() {
    if (serverSocket == null) {
      throw new IllegalStateException("Server socket can't be null.");
    }
    // Set a timeout on the accept so we can catch shutdown requests.
    try {
      this.serverSocket.setSoTimeout(1000);
    } catch (SocketException e) {
      e.printStackTrace();
      LOGGER.info("Unable to set acceptor timeout value. The server may not shutdown gracefully.");
    }

    LOGGER.info("Accepting incoming connections on port " + this.serverSocket.getLocalPort());

    // Accept an incoming connection , handle it, then close and repeat.
    while (isRunning()) {
      try {
        // Accept the next incoming connection
        final Socket clientSocket = this.serverSocket.accept();
        LOGGER.info("Accepted connection from " + clientSocket.getRemoteSocketAddress());

        final ConnectionHandler handler = new ConnectionHandler(clientSocket);
        if (clientSocket.isConnected()) {
          LOGGER.info("isConnected");

          this.onClientConnected(handler);
          handler.addListener(this);
          this.workers.execute(handler);
        } else {
          LOGGER.warn("Server socket isn't connected.");
        }
      } catch (SocketTimeoutException ste) {
        LOGGER.info("Ignored, timeouts will happen every 1 second");
      } catch (IOException e) {
        e.printStackTrace();
        // Yield to other threads if an exception occurs (prevent CPU spin)
        LOGGER.trace("Yield to other threads if an exception occurs (prevent CPU spin)");
        Thread.yield();
      }
    }
    try {
      // Make sure to release the port, otherwise it may remain bound for several minutes
      this.serverSocket.close();
    } catch (IOException e) {
      // Ignored
    }
    LOGGER.info("Stopped accepting incoming connections.");
  }

  /**
   * Verifies if server is running.
   *
   * @return true if server is running; otherwise, false.
   */
  private synchronized boolean isRunning() {
    return this.keepRunning;
  }

  /**
   * Stops server.
   */
  private synchronized void stopRunning() {
    this.keepRunning = false;
  }

  /**
   * Shuts down this server. Since the main server thread will time out every 1 second,
   * the shutdown process should complete in at most 1 second from the time this method is invoked.
   */
  public void shutdown() {
    LOGGER.info("Shutting down the server");
    stopRunning();
    this.workers.shutdownNow();
    try {
      this.join();
    } catch (InterruptedException exc) {
      // Ignored, we're exiting anyway.
    }
  }

  /**
   * Notify all listeners about a received message event.
   *
   * @param connection
   * @param message
   */
  @Override
  public void onMessageReceived(final TcpConnection connection, final Object message) {
    LOGGER.trace("Received new message from " + connection.getSocketAddress().getHostName() + ": " +
        "" + connection.getSocketAddress().getPort());
    LOGGER.trace("Class name: " + message.getClass().getCanonicalName() + ", toString: " +
        message.toString());
    for (final TcpConnection.Listener listener : listeners) {
      listener.onMessageReceived(connection, message);
    }
  }

  /**
   * Notify all listeners about a connected client event.
   *
   * @param connection
   */
  @Override
  public void onClientConnected(TcpConnection connection) {
    LOGGER.info("onClientConnected");
    LOGGER.info("New connection! " + connection.getSocketAddress().getHostName() + ": " +
        connection.getSocketAddress().getPort() + ".");
    connections.add(connection);
    LOGGER.info("Current connections count: " + connections.size());
    for (final TcpConnection.Listener listener : listeners) {
      listener.onClientConnected(connection);
    }
  }

  /**
   * Notify all listeners about a disconnected client event.
   *
   * @param connection
   */
  @Override
  public void onClientDisconnected(TcpConnection connection) {
    LOGGER.info("Disconnect! Ip: " + connection.getSocketAddress().getHostName() + ": " +
        connection.getSocketAddress().getPort() + ".");
    connections.remove(connection);
    LOGGER.info("Current connections count: " + connections.size());
    for (final TcpConnection.Listener listener : listeners) {
      listener.onClientDisconnected(connection);
    }
  }

  /**
   * Get the number of active connections.
   *
   * @return
   */
  @Override
  public int getConnectionsCount() {
    return connections.size();
  }

  /**
   * Configures the server port. It uses 6969 as a default port.
   *
   * @param port
   */
  @Override
  public void setPort(final Integer port) {
    try {
      if (port == null) {
        LOGGER.info("Property tcp.server.port not found. Use default port 6969");
        this.port = securePort;//DEFAULT_PORT;
      }
      this.port = port;
      serverSocket = new ServerSocket(this.port);
      LOGGER.info("Server start at port " + port);
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error("May be port " + port + " busy.");
    }
  }

  /**
   * Retrieves all connections.
   *
   * @return a list of connections.
   */
  @Override
  public List<TcpConnection> getConnections() {
    return connections;
  }

  /**
   * Adds a {@code {@link TcpConnection.Listener}}.
   *
   * @param listener
   */
  @Override
  public void addListener(final TcpConnection.Listener listener) {
    this.listeners.add(listener);
  }
}

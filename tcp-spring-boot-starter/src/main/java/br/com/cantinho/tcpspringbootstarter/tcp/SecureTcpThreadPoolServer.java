package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Listens for connections on a specified port.
 * Reads a line from the newly-connected socket.
 * Converts the line to uppercase and responds on the socket.
 * Repeats the previous step until the connection is closed by the client.
 */
@Component(value = "SecureTcpThreadPoolServer")
public class SecureTcpThreadPoolServer extends Thread implements TcpServer, TcpConnection.Listener {

  /**
   * Default server port.
   */
  private static final int DEFAULT_PORT = 8080;

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SecureTcpThreadPoolServer.class);

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
      this.serverSocket = new ServerSocket(port);
      LOGGER.info("Server start at port " + port);
    } catch (IOException e) {
      LOGGER.warn("ignored\n" + e.getMessage());
      LOGGER.error("May be port " + port + " busy.");
    }
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
        this.port = DEFAULT_PORT;
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

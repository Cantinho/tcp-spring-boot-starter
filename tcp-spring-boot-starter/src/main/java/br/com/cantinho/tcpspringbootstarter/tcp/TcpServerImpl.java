package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP server implementation.
 */
@Component(value = "TcpServerImpl")
public class TcpServerImpl implements TcpServer, TcpConnection.Listener {

  private static final int DEFAULT_PORT = 6969;

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerImpl.class);

  /**
   * Server socket.
   */
  private ServerSocket serverSocket;

  /**
   * If server is running.
   */
  private volatile boolean isStop;

  /**
   * All TCP connections.
   */
  private List<TcpConnection> connections = new ArrayList<>();

  /**
   * All TCP connection listeners.
   */
  private List<TcpConnection.Listener> listeners = new ArrayList<>();

  /**
   * Configures the server port.
   *
   * @param port
   */
  public void setPort(Integer port) {
    try {
      if (port == null) {
        LOGGER.info("Property tcp.server.port not found. Use default port 1234");
        port = DEFAULT_PORT;
      }
      serverSocket = new ServerSocket(port);
      LOGGER.info("Server start at port " + port);
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error("May be port " + port + " busy.");
    }
  }

  /**
   * Retrieve all connections count.
   *
   * @return
   */
  @Override
  public int getConnectionsCount() {
    return connections.size();
  }

  /**
   * Starts server.
   */
  @Override
  public void start() {
    new Thread(() -> {
      while (!isStop) {
        try {
          Socket socket = serverSocket.accept();
          if (socket.isConnected()) {
            TcpConnectionImpl tcpConnection = new TcpConnectionImpl(socket);
            tcpConnection.start();
            tcpConnection.addListener(this);
            onClientConnected(tcpConnection);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * Stops server.
   */
  @Override
  public void stop() {
    isStop = true;
  }

  /**
   * Retrieve all connections.
   *
   * @return
   */
  @Override
  public List<TcpConnection> getConnections() {
    return connections;
  }

  /**
   * Add a TCP connection listener.
   *
   * @param listener
   */
  @Override
  public void addListener(final TcpConnection.Listener listener) {
    listeners.add(listener);
  }

  /**
   * Called when message is received.
   *
   * @param connection
   * @param message
   */
  @Override
  public void onMessageReceived(final TcpConnection connection, final Object message) {
    LOGGER.trace("Received new message from " + connection.getSocketAddress().getHostName() + ": " + connection.getSocketAddress().getPort());
    LOGGER.trace("Class name: " + message.getClass().getCanonicalName() + ", toString: " + message.toString());
    for (TcpConnection.Listener listener : listeners) {
      listener.onMessageReceived(connection, message);
    }
  }

  /**
   * Called when connected client event is received.
   *
   * @param connection
   */
  @Override
  public void onClientConnected(final TcpConnection connection) {
    LOGGER.info("New connection! " + connection.getSocketAddress().getHostName() + ": " + connection.getSocketAddress().getPort()+".");
    connections.add(connection);
    LOGGER.info("Current connections count: " + connections.size());
    for (TcpConnection.Listener listener : listeners) {
      listener.onClientConnected(connection);
    }
  }

  /**
   * Called when disconnected client event is received.
   * @param connection
   */
  @Override
  public void onClientDisconnected(final TcpConnection connection) {
    LOGGER.info("Disconnect! Ip: " + connection.getSocketAddress().getHostName() + ": " + connection.getSocketAddress().getPort()+ ".");
    connections.remove(connection);
    LOGGER.info("Current connections count: " + connections.size());
    for (TcpConnection.Listener listener : listeners) {
      listener.onClientDisconnected(connection);
    }
  }

}

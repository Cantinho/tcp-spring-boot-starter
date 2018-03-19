package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple runnable class that performs the basic work of this server.
 * It will read a line from the client, convert it to uppercase, and then write it back to the
 * client.
 */
public final class ConnectionHandler implements TcpConnection, Runnable {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandler.class);

  /**
   * The socket connected to the client
   */
  private final Socket clientSocket;

  /**
   * Input stream.
   */
  private InputStream input;

  /**
   * Output stream.
   */
  private OutputStream output;

  /**
   * Tcp listeners.
   */
  private List<Listener> listeners = new ArrayList<>();

  /**
   * Builds a custon Connection handler.
   *
   * @param socket
   */
  public ConnectionHandler(final Socket socket) {

    this.clientSocket = socket;
    try {
      input = socket.getInputStream();
      output = socket.getOutputStream();
    } catch (IOException ioe) {
      throw new IllegalStateException("Could not initialize TcpConnection for socket " + socket,
          ioe);
    }
  }

  /**
   * The run method is invoked by the Executor Service (thread pool).
   */
  @Override
  public void run() {
    if (listeners == null || listeners.isEmpty()) {
      LOGGER.warn("No one could receive any message.");
      throw new IllegalStateException("No server listener was registered.");
    }

    LOGGER.debug("run");
    while (true) {
      byte[] buffer = new byte[64 * 1024];
      try {
        int count = input.read(buffer);
        LOGGER.debug("read - data count:" + count);
        if (count > 0) {
          byte[] bytes = Arrays.copyOf(buffer, count);
          for (final Listener listener : listeners) {
            LOGGER.debug("onMessageReceive:" + bytes.length + " bytes.");
            listener.onMessageReceived(this, bytes);
          }
        } else {
          clientSocket.close();
          for (final Listener listener : listeners) {
            listener.onClientDisconnected(this);
          }
          break;
        }
      } catch (IOException ioe) {
        LOGGER.trace("Could not receive message from TcpConnection for socket " + clientSocket,
            ioe);

        closeStream(input);
        closeStream(output);

        for (final Listener listener : listeners) {
          listener.onClientDisconnected(this);
        }
        break;
      }
    }
  }

  /**
   * Close
   *
   * @param closeable
   */
  private void closeStream(final Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Remote socket address.
   *
   * @return
   */
  @Override
  public InetSocketAddress getSocketAddress() {
    LOGGER.debug("getSocketAddress");
    return (InetSocketAddress) clientSocket.getRemoteSocketAddress();
  }

  /**
   * Send a object to client.
   *
   * @param objectToSend
   */
  @Override
  public void send(final Object objectToSend) {
    LOGGER.debug("send");
    if (objectToSend instanceof byte[]) {
      byte[] data = (byte[]) objectToSend;
      try {
        output.write(data);
      } catch (IOException ioe) {
        LOGGER.error("Could not send object to TcpConnection for socket " + clientSocket, ioe);
      }
    }
  }

  @Override
  public void addListener(final Listener listener) {
    LOGGER.debug("addListener");
    listeners.add(listener);
  }

  @Override
  public void start() {
    LOGGER.debug("start");
  }

  /**
   * Closes socket.
   */
  @Override
  public void close() {
    LOGGER.debug("close");
    try {
      clientSocket.close();
    } catch (IOException ioe) {
      LOGGER.error("Could not close TcpConnection for socket " + clientSocket, ioe);
    }
  }
}

package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A TCP connection implementation.
 */
public class TcpConnectionImpl implements TcpConnection {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpConnectionImpl.class);

  /**
   * An input stream to receive data from client.
   */
  private InputStream input;

  /**
   * An output stream to send data to client.
   */
  private OutputStream output;

  /**
   * A socket.
   */
  private Socket socket;

  /**
   * TCP connection listeners.
   */
  private List<Listener> listeners = new ArrayList<>();

  /**
   * Builds a custom tcp connection basic implementation.
   *
   * @param socket
   */
  public TcpConnectionImpl(final Socket socket) {
    LOGGER.info("construct");
    this.socket = socket;
    try {
      input = socket.getInputStream();
      output = socket.getOutputStream();
    } catch (IOException ioe) {
      throw new IllegalStateException("Could not initialize TcpConnection for socket " + socket, ioe);
    }
  }

  /**
   * Retrieves a socket address.
   *
   * @return
   */
  @Override
  public InetSocketAddress getSocketAddress() {
    return (InetSocketAddress) socket.getRemoteSocketAddress();
  }

  /**
   * Sends an object to client.
   *
   * @param objectToSend
   */
  @Override
  public void send(final Object objectToSend) {

    if(objectToSend instanceof byte[]) {
      byte[] data = (byte[]) objectToSend;
      try {
        output.write(data);
      } catch (IOException ioe) {
        LOGGER.error("Could not send object to TcpConnection for socket " + socket, ioe);
      }
    }
  }

  /**
   * Adds a TCP connection listener.
   * @param listener
   */
  @Override
  public void addListener(final Listener listener) {
    listeners.add(listener);
  }

  /**
   * Starts a simple server.
   */
  @Override
  public void start() {
    new Thread(() -> {
      while (true) {
        byte[] buffer = new byte[64*1024];
        try {
          int count = input.read(buffer);
          if(count > 0) {
            byte[] bytes = Arrays.copyOf(buffer, count);
            for(final Listener listener : listeners) {
              listener.onMessageReceived(this, bytes);
            }
          } else {
            socket.close();
            for(final Listener listener : listeners) {
              listener.onClientDisconnected(this);
            }
            break;
          }
        } catch (IOException ioe) {
          LOGGER.trace("Could not receive message from TcpConnection for socket " + socket, ioe);
          for(final Listener listener : listeners) {
            listener.onClientDisconnected(this);
          }
          break;
        }
      }
    }).start();
  }

  /**
   * Closes connection.
   */
  @Override
  public void close() {
    try {
      socket.close();
    } catch (IOException ioe) {
      LOGGER.error("Could not close TcpConnection for socket " + socket, ioe);
    }
  }
}

package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * TCP controller listener.
 */
public class TcpControllerListener implements TcpConnection.Listener {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpControllerListener.class);

  /**
   * Bean.
   */
  private final Object bean;

  /**
   * All methods started with receive word.
   */
  private final List<Method> receiveMethods;

  /**
   * All methods started with connect word.
   */
  private final List<Method> connectMethods;

  /**
   * All methods started with disconnected word.
   */
  private final List<Method> disconnectedMethods;

  public TcpControllerListener(final Object bean,
                               final List<Method> receiveMethods,
                               final List<Method> connectMethods,
                               final List<Method> disconnectedMethods) {
    this.bean = bean;
    this.receiveMethods = receiveMethods;
    this.connectMethods = connectMethods;
    this.disconnectedMethods = disconnectedMethods;
  }

  /**
   * Called when message is received.
   *
   * @param connection
   * @param message
   */
  @Override
  public void onMessageReceived(final TcpConnection connection, final Object message) {
    LOGGER.info("onMessageReceived");
    for(final Method receiveMethod : receiveMethods) {
      final Class<?> aClass = receiveMethod.getParameterTypes()[1];
      if(aClass.isAssignableFrom(message.getClass())) {
        try {
          receiveMethod.invoke(bean, connection, message);
        } catch (IllegalAccessException | InvocationTargetException ite) {
          LOGGER.error("Could not process received message " + message + " for connection " +
              connection.getSocketAddress());
        }
      }
    }
  }

  /**
   * Called when client is connected.
   *
   * @param connection
   */
  @Override
  public void onClientConnected(final TcpConnection connection) {
    LOGGER.info("onClientConnected");
    for(final Method connectMethod : connectMethods) {
      try {
        connectMethod.invoke(bean, connection);
      } catch (IllegalAccessException | InvocationTargetException exc) {
        LOGGER.error("Could not process client connection for connection " + connection
            .getSocketAddress(), exc);
      }
    }
  }

  /**
   * Called when client is disconnected.
   *
   * @param connection
   */
  @Override
  public void onClientDisconnected(final TcpConnection connection) {
    LOGGER.info("onClientDisconnected");
    for(final Method disconnectedMethod : disconnectedMethods) {
      try {
        disconnectedMethod.invoke(bean, connection);
      } catch (IllegalAccessException | InvocationTargetException exc) {
        LOGGER.error("Could not process client disconnect for connection " + connection
            .getSocketAddress(), exc);
      }
    }
  }
}

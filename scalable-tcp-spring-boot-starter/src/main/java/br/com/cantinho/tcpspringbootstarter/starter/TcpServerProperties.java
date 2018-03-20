package br.com.cantinho.tcpspringbootstarter.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TCP server properties.
 */
@ConfigurationProperties(prefix = "tcp.server")
public class TcpServerProperties {

  /**
   * Server port.
   */
  private int port;

  /**
   * Enable autostart.
   * TODO fix this.
   */
  private boolean autoStart;

  /**
   * Retrieves the server port.
   *
   * @return
   */
  public int getPort() {
    return port;
  }

  /**
   * Configures the server port.
   *
   * @param port
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Retrieves auto start flag.
   *
   * @return
   */
  public boolean getAutoStart() {
    return autoStart;
  }

  /**
   * Configures auto start flag.
   *
   * @param autoStart
   */
  public void setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
  }

}

package br.com.cantinho.tcpspringbootstarter.tcp;

public class TcpException extends Exception {
  public TcpException() {
  }

  public TcpException(String message) {
    super(message);
  }

  public TcpException(String message, Throwable cause) {
    super(message, cause);
  }
}

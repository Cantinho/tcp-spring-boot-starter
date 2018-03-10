package br.com.cantinho.tcpspringbootstarter.data;

/**
 * Data handler exception.
 */
public class DataHandlerException extends Exception {

  /**
   * Builds a data handler exception passing a message as argument.
   *
   * @param message
   */
  public DataHandlerException(final String message) {
    super(message);
  }

}

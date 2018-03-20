package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class InvalidParameterException extends Exception {

  private String parameter;

  public InvalidParameterException(final String parameter) {
    this.parameter = parameter;
  }

  public InvalidParameterException(final String parameter, final String message) {
    super(message);
    this.parameter = parameter;
  }

  public String getParameter() {
    return parameter;
  }

}

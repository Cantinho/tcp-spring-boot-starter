package br.com.cantinho.tcpspringbootstarter;

public final class ApplicationConfig {

  public static final String SECURE_SERVER_IMPLEMENTATION = "SecureTcpThreadPoolServer";
  public static final String NON_SECURE_SERVER_IMPLEMENTATION = "TcpThreadPoolServer";

  public static final String CURRENT_SERVER_IMPLEMENTATION = NON_SECURE_SERVER_IMPLEMENTATION;
}

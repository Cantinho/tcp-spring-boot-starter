package br.com.cantinho.tcpspringbootstarter.config;

public final class ApplicationConfig {

  public static final String SECURE_SERVER_IMPLEMENTATION = "SecureTcpThreadPoolServer";
  public static final String NON_SECURE_SERVER_IMPLEMENTATION = "TcpThreadPoolServer";

  public static final String CURRENT_SERVER_IMPLEMENTATION = SECURE_SERVER_IMPLEMENTATION;
}

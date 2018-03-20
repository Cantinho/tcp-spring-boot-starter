package br.com.cantinho.tcpspringbootstarter.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ssl.properties")
public class SSLProperties {

  /**
   * IP of foo service used to blah.
   */
  private String credentials = "";

  // getter & setter

  public String getCredentials() {
    return credentials;
  }

  public void setCredentials(String credentials) {
    this.credentials = credentials;
  }
}
package br.com.cantinho.tcpspringbootstarter.converters;

import java.util.Objects;

public class V1Data extends Versionable {

  private String source;

  private String destination;

  private String payload;

  public V1Data(){}

  public V1Data(final String source, final String destination, final String payload) {
    super(V1Data.class.getCanonicalName());
    this.source = source;
    this.destination = destination;
    this.payload = payload;
  }

  @Override
  public String getVer() {
    return V1Data.class.getCanonicalName();
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }

  public String getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "V1Data{" +
        "source='" + source + '\'' +
        ", destination='" + destination + '\'' +
        ", payload='" + payload + '\'' +
        '}';
  }

}

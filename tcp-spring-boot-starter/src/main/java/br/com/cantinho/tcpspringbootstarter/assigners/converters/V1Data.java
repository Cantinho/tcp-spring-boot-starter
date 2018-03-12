package br.com.cantinho.tcpspringbootstarter.assigners.converters;

public class V1Data extends Versionable {

  protected static final String VERSION = V1Data.class.getSimpleName();

  private String source;

  private String destination;

  private String payload;

  public V1Data(){}

  public V1Data(final String source, final String destination, final String payload) {
    super(VERSION);
    this.source = source;
    this.destination = destination;
    this.payload = payload;
  }

  @Override
  public String getVer() {
    return VERSION;
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

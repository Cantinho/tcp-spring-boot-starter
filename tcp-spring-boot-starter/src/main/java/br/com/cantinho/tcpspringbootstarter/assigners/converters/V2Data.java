package br.com.cantinho.tcpspringbootstarter.assigners.converters;

public class V2Data extends Versionable {

  protected static final String VERSION = V2Data.class.getSimpleName();

  private String src;

  private String dest;

  private String data;

  public V2Data(){}

  public V2Data(final String src, final String dest, final String data) {
    super(VERSION);
    this.src = src;
    this.dest = dest;
    this.data = data;
  }

  @Override
  public String getVer() {
    return VERSION;
  }

  public String getSrc() {
    return src;
  }

  public String getDest() {
    return dest;
  }

  public String getData() {
    return data;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public void setDest(String dest) {
    this.dest = dest;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "V1Data{" +
        "src='" + src + '\'' +
        ", dest='" + dest + '\'' +
        ", data='" + data + '\'' +
        '}';
  }

}

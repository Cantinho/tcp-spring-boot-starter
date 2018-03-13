package br.com.cantinho.domain;

public class V2Data extends Versionable {

  private String src;
  private String dest;
  private String data;

  public V2Data(){}

  public V2Data(final String src, final String dest, final String data) {
    super(V2Data.class.getSimpleName());
    this.src = src;
    this.dest = dest;
    this.data = data;
  }

  @Override
  public String getVer() {
    return V2Data.class.getSimpleName();
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

  @Override
  public String toString() {
    return "V1Data{" +
        "src='" + src + '\'' +
        ", dest='" + dest + '\'' +
        ", data='" + data + '\'' +
        '}';
  }

}

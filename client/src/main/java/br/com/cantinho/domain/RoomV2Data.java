package br.com.cantinho.domain;

public class RoomV2Data extends Versionable {

  protected static final String VERSION = RoomV2Data.class.getSimpleName();

  private String f;
  private String t;
  private String m;

  public RoomV2Data() {
    super(VERSION);
  }

  public RoomV2Data(String f, String t, String m) {
    super(VERSION);
    this.f = f;
    this.t = t;
    this.m = m;
  }

  public String getF() {
    return f;
  }

  public void setF(String f) {
    this.f = f;
  }

  public String getT() {
    return t;
  }

  public void setT(String t) {
    this.t = t;
  }

  public String getM() {
    return m;
  }

  public void setM(String m) {
    this.m = m;
  }

  @Override
  public String toString() {
    return "RoomV2Data{" +
        "f='" + f + '\'' +
        ", t='" + t + '\'' +
        ", m='" + m + '\'' +
        '}';
  }
}

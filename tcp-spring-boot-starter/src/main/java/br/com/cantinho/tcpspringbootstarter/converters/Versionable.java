package br.com.cantinho.tcpspringbootstarter.converters;

public abstract class Versionable {

  private String ver;

  public Versionable() {}

  public Versionable(final String ver) {
    this.ver = ver;
  }

  public String getVer() {
    return ver;
  }

}
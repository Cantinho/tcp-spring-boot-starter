package br.com.cantinho.domain;

public class Versionable {

  private String ver;

  public Versionable() {}

  public Versionable(final String ver) {
    this.ver = ver;
  }

  public String getVer() {
    return ver;
  }

}

package br.com.cantinho.tcpspringbootstarter.converters;

public abstract class IConverter {

  public abstract String version();

  public abstract Object parse(byte[] data);

}
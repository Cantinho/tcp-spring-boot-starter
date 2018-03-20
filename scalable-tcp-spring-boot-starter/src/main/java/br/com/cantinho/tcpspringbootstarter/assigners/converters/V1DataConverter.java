package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import com.google.gson.Gson;

public class V1DataConverter extends IConverter {

  @Override
  public String version() {
    return V1Data.VERSION;
  }

  @Override
  public Object parse(byte[] rawData) {
    try {
      return new Gson().fromJson(new String(rawData), V1Data.class);
    } catch (Exception exc) {
      return null;
    }
  }
}

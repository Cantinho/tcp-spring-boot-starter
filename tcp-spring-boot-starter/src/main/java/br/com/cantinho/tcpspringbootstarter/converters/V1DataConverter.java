package br.com.cantinho.tcpspringbootstarter.converters;

import com.google.gson.Gson;

public class V1DataConverter extends IConverter {

  @Override
  public String version() {
    return V1Data.class.getCanonicalName();
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

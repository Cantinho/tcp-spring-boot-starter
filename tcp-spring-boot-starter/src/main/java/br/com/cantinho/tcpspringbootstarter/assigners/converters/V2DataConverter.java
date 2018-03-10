package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import com.google.gson.Gson;

public class V2DataConverter extends IConverter {

  @Override
  public String version() {
    return V2Data.class.getCanonicalName();
  }

  @Override
  public Object parse(byte[] rawData) {
    try {
      return new Gson().fromJson(new String(rawData), V2Data.class);
    } catch (Exception exc) {
      return null;
    }
  }
}

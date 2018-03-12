package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import com.google.gson.Gson;

public class RoomV1DataConverter extends IConverter {

  @Override
  public String version() {
    return RoomV1Data.VERSION;
  }

  @Override
  public Object parse(byte[] rawData) {
    try {
      return new Gson().fromJson(new String(rawData), RoomV1Data.class);
    } catch (Exception exc) {
      return null;
    }
  }
}

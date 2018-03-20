package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import com.google.gson.Gson;

public class RoomV2DataConverter extends IConverter {

  @Override
  public String version() {
    return RoomV2Data.VERSION;
  }

  @Override
  public Object parse(byte[] rawData) {
    try {
      return new Gson().fromJson(new String(rawData), RoomV2Data.class);
    } catch (Exception exc) {
      return null;
    }
  }
}

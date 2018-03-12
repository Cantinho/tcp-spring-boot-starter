package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import java.lang.reflect.Method;

public class RoomDataConverter {

  public static RoomData jsonize(final Object object) throws Exception {
    final Class clazz = object.getClass();

    RoomData roomData = new RoomData();

    if(RoomV1Data.class.equals(clazz)) {
      final Method getSource = object.getClass().getDeclaredMethod("getFrom");
      final Method getDestination = object.getClass().getDeclaredMethod("getTo");
      final Method getPayload = object.getClass().getDeclaredMethod("getMsg");
      roomData.setFrom((String) getSource.invoke(object));
      roomData.setTo((String) getDestination.invoke(object));
      roomData.setMsg((String) getPayload.invoke(object));
    } else {
      throw new Exception("fail");
    }
    return roomData;
  }

  public static Object dejsonizeFrom(final Class clazz, RoomData obj) throws Exception {

    final String className = clazz.getSimpleName();

    switch (className) {
      case RoomDataConverter.DataType.ROOMV1DATA: {
        final RoomV1Data v1Data = new RoomV1Data();
        v1Data.setFrom(obj.getFrom());
        v1Data.setTo(obj.getTo());
        v1Data.setMsg(obj.getMsg());
        return v1Data;
      }
      default:
        throw new Exception("fail.");
    }

  }

  private static class DataType {
    private static final String ROOMV1DATA = "RoomV1Data";
  }

}

package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

public class RoomDataConverter {

  public static RoomData jsonize(final Object object) throws Exception {
    final Class clazz = object.getClass();

    System.out.println("-------> " + new ObjectMapper().writeValueAsString(object));

    RoomData roomData = new RoomData();

    if(RoomV1Data.class.equals(clazz)) {
      final Method getSource = object.getClass().getDeclaredMethod("getFrom");
      final Method getDestination = object.getClass().getDeclaredMethod("getTo");
      final Method getPayload = object.getClass().getDeclaredMethod("getMsg");
      roomData.setFrom((String) getSource.invoke(object));
      roomData.setTo((String) getDestination.invoke(object));
      roomData.setMsg((String) getPayload.invoke(object));
    } else if(RoomV2Data.class.equals(clazz)) {
      final Method getSource = object.getClass().getDeclaredMethod("getF");
      final Method getDestination = object.getClass().getDeclaredMethod("getT");
      final Method getPayload = object.getClass().getDeclaredMethod("getM");
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
      case RoomDataConverter.DataType.ROOMV2DATA: {
        final RoomV2Data v2Data = new RoomV2Data();
        v2Data.setF(obj.getFrom());
        v2Data.setT(obj.getTo());
        v2Data.setM(obj.getMsg());
        return v2Data;
      }
      default:
        throw new Exception("fail.");
    }

  }

  private static class DataType {
    private static final String ROOMV1DATA = "RoomV1Data";
    private static final String ROOMV2DATA = "RoomV2Data";
  }

}

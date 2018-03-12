package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import java.lang.reflect.Method;

public class EchoDataConverter {

  public static EchoData jsonize(final Object object) throws Exception {
    final Class clazz = object.getClass();

    EchoData echoData = new EchoData();

    if(V1Data.class.equals(clazz)) {
      final Method getSource = object.getClass().getDeclaredMethod("getSource");
      final Method getDestination = object.getClass().getDeclaredMethod("getDestination");
      final Method getPayload = object.getClass().getDeclaredMethod("getPayload");
      echoData.setSource((String) getSource.invoke(object));
      echoData.setDestination((String) getDestination.invoke(object));
      echoData.setPayload((String) getPayload.invoke(object));
    } else if (V2Data.class.equals(clazz)) {
      final Method getSrc = object.getClass().getDeclaredMethod("getSrc");
      final Method getDest = object.getClass().getDeclaredMethod("getDest");
      final Method getData = object.getClass().getDeclaredMethod("getData");
      echoData.setSource((String) getSrc.invoke(object));
      echoData.setDestination((String) getDest.invoke(object));
      echoData.setPayload((String) getData.invoke(object));
    } else {
      throw new Exception("fail");
    }
    return echoData;
  }

  public static Object dejsonizeFrom(final Class clazz, EchoData obj) throws Exception {

    final String className = clazz.getSimpleName();

    switch (className) {
      case EchoDataConverter.DataType.V1DATA: {
        final V1Data v1Data = new V1Data();
        v1Data.setSource(obj.getSource());
        v1Data.setDestination(obj.getDestination());
        v1Data.setPayload(obj.getPayload());
        return v1Data;
      }
      case EchoDataConverter.DataType.V2DATA: {
        final V2Data v2Data = new V2Data();
        v2Data.setSrc(obj.getSource());
        v2Data.setDest(obj.getDestination());
        v2Data.setData(obj.getPayload());
        return v2Data;
      }
      default:
        throw new Exception("fail.");
    }

  }

  private static class DataType {
    private static final String V1DATA = "V1Data";
    private static final String V2DATA = "V2Data";
  }

}

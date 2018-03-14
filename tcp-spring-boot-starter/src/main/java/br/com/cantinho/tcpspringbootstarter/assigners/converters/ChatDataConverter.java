package br.com.cantinho.tcpspringbootstarter.assigners.converters;

import java.lang.reflect.Method;

public class ChatDataConverter {

  public static ChatData jsonize(final Object object) throws Exception {
    final Class clazz = object.getClass();
    final ChatData chatData = new ChatData();

    if(ChatV1Data.class.equals(clazz)) {
      final Method getSource = object.getClass().getDeclaredMethod("getFrom");
      final Method getDestination = object.getClass().getDeclaredMethod("getTo");
      final Method getCommand = object.getClass().getDeclaredMethod("getCmd");
      final Method getPayload = object.getClass().getDeclaredMethod("getMsg");
      chatData.setFrom((String) getSource.invoke(object));
      chatData.setTo((String) getDestination.invoke(object));
      chatData.setCmd((String) getCommand.invoke(object));
      chatData.setMsg((String) getPayload.invoke(object));
    } else {
      throw new Exception("fail");
    }
    return chatData;
  }

  public static Object dejsonizeFrom(final Class clazz, final ChatData obj) throws Exception {

    final String className = clazz.getSimpleName();

    switch (className) {
      case ChatDataConverter.DataType.CHATV1DATA: {
        final ChatV1Data v1Data = new ChatV1Data();
        v1Data.setFrom(obj.getFrom());
        v1Data.setTo(obj.getTo());
        v1Data.setCmd(obj.getCmd());
        v1Data.setMsg(obj.getMsg());
        return v1Data;
      }
      default:
        throw new Exception("fail.");
    }

  }

  private static class DataType {
    private static final String CHATV1DATA = "ChatV1Data";
  }

}

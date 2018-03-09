package br.com.cantinho.tcpspringbootstarter.converters;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DataHandler {


  private List<IConverter> dataConverters = new ArrayList<>();

  public String onIncomingData(final String connectionUuid, final byte[] data) throws
      DataHandlerException {
    final Versionable versionable = getVersionable(data);
    if(dataConverters.isEmpty()) {
      throw new DataHandlerException("It could not find a suitable converter.");
    }
    for(final IConverter converter : dataConverters) {
      final boolean equals = compareVersion(versionable.getVer(), converter.version());
      if(equals) {
        final String version = converter.version();
        final Object message = converter.parse(data);

      }
    }
    return null;

  }

  private Versionable getVersionable(final byte[] rawData) throws DataHandlerException {
    try {
      final Versionable versionable = new Gson().fromJson(new String(rawData), Versionable.class);
      return versionable;
    } catch (JsonSyntaxException exc) {
      final Versionable byteProtocol = new Versionable() {
        @Override
        public String getVer() {
          return "TODO";
        }
      };
      return byteProtocol;
    } catch (Exception exc) {
      throw new DataHandlerException("Data can't be parsed. Message:" + exc.getMessage());
    }
  }

  boolean compareVersion(final String obj, final String other) throws DataHandlerException {
    if(obj == null || other == null) {
      throw new DataHandlerException("Version can't be null.");
    }
    return obj.compareTo(other) == 0;
  }


}

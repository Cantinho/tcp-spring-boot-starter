package br.com.cantinho.tcpspringbootstarter.data;

import br.com.cantinho.tcpspringbootstarter.assigners.Assignable;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.Versionable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataHandler {

  /**
   * A logger instance.
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(DataHandler.class.getCanonicalName());

  private List<Assignable> assignables;

  public DataHandler(final List<Assignable> assignables) throws DataHandlerException {
    if(null == assignables || assignables.isEmpty()) {
      throw new DataHandlerException("It could not find a suitable assignable.");
    }
    this.assignables = assignables;
  }


  /**
   * Assigns data to only one assignable.
   * @param uci
   * @param data
   * @throws DataHandlerException
   */
  public void onIncomingData(final String uci, final byte[] data) throws
      DataHandlerException {
    final Versionable versionable = getVersionable(data);
    for(final Assignable assignable : assignables) {
      for(final String version : assignable.getVersions()) {
        final boolean equals = compareVersion(versionable.getVer(), version);
        if(equals) {
          final Object message = assignable.parse(data);
          assignable.assign(uci, message);
          return;
        }
      }
    }
    LOGGER.info("Data handler can't find any suitable assignable. UCI:{}", uci);
  }

  public static Versionable getVersionable(final byte[] rawData) throws DataHandlerException {
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

  public static boolean compareVersion(final String obj, final String other) throws
      DataHandlerException {
    if(obj == null || other == null) {
      throw new DataHandlerException("Version can't be null.");
    }
    return obj.compareTo(other) == 0;
  }


}

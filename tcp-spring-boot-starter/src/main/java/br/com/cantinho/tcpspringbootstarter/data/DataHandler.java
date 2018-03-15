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
  private static final Logger LOGGER = LoggerFactory.getLogger(DataHandler.class.getCanonicalName());

  /**
   * Connect event.
   */
  public static final int CONNECT = 1;

  /**
   * Disconnect event.
   */
  public static final int DISCONNECT = 2;

  /**
   * Incoming data event.
   */
  public static final int DATA = 3;

  /**
   * Assignables list.
   */
  private List<Assignable> assignables;

  private DataHandler() {
    // We don't provide empty constructor.
  }

  /**
   * Builds a data handler passing assignables as argument.
   *
   * @param assignables
   * @throws DataHandlerException
   */
  public DataHandler(final List<Assignable> assignables) throws DataHandlerException {
    if(null == assignables || assignables.isEmpty()) {
      throw new DataHandlerException("It could not find a suitable assignable.");
    }
    this.assignables = assignables;
  }

  /**
   * Broadcasts connect event for all assignables.
   *
   * @param parameters uci
   */
  public void onConnect(final Object... parameters) {
    final String uci = (String) parameters[0];
    for(final Assignable assignable : assignables) {
      assignable.onConnect(uci);
    }
  }

  /**
   * Broadcasts disconnect event for all assignables.
   *
   * @param parameters uci
   */
  public void onDisconnect(final Object... parameters) {
    final String uci = (String) parameters[0];
    for(final Assignable assignable : assignables) {
      assignable.onDisconnect(uci);
    }
  }

  /**
   * Assigns data to only one assignable.
   *
   * @param parameters unique connection identifier.
   * @param parameters optional
   * @throws DataHandlerException
   */
  public void onIncomingData(final Object... parameters) throws DataHandlerException {
    final String uci = (String) parameters[0];
    final byte[] data = (byte[]) parameters[1];

    final Versionable versionable = getVersionable(data);
    for(final Assignable assignable : assignables) {
      for(final String version : assignable.getVersions()) {
        final boolean equals = compareVersion(versionable.getVer(), version);
        if(equals) {
          final Object message = assignable.parse(data);
          if(null != message) {
            assignable.assign(uci, message, versionable.getClass().getSimpleName());
          } else {
            LOGGER.error("message cannot be null.");
          }

          return;
        }
      }
    }
    LOGGER.info("Data handler can't find any suitable assignable. UCI:{}", uci);
  }



  /**
   * Retrieves the versionable from data.
   * Tries to find a versionable of the data.
   * If there's no suitable candidate, a data handler exceptions will be emitted.
   *
   * TODO: The {@code getVersionable} must understand how gets version if data is non-JSON parsable.
   * TODO: In case of binary data, so header content, header size and checksum  metadata must be
   * TODO: supplied.
   *
   * @param rawData
   * @return
   * @throws DataHandlerException
   */
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

  /**
   * Compares version between string objects.
   *
   * @param obj
   * @param other
   * @return
   * @throws DataHandlerException
   */
  public static boolean compareVersion(final String obj, final String other) throws
      DataHandlerException {
    if(obj == null || other == null) {
      throw new DataHandlerException("Version can't be null.");
    }
    return obj.compareTo(other) == 0;
  }


}

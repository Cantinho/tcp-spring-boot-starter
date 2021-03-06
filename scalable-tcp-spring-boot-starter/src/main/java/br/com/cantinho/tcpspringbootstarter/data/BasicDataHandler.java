package br.com.cantinho.tcpspringbootstarter.data;

import br.com.cantinho.tcpspringbootstarter.assigners.Assignable;
import java.util.List;

/**
 * A basid data handler implementation.
 */
public class BasicDataHandler extends DataHandler {

  /**
   * Builds a data handler passing assignables as argument.
   *
   * @param assignables
   * @throws DataHandlerException
   */
  public BasicDataHandler(final List<Assignable> assignables) throws DataHandlerException {
    super(assignables);
  }

  /**
   * Assigns connection to everyone.
   *
   * @param uci unique connection event identifier.
   * @throws DataHandlerException
   */
  public void onConnect(final String uci) throws
      DataHandlerException {
    super.onConnect(uci);
  }

  /**
   * Assigns disconnection event to everyone.
   *
   * @param uci unique connection identifier.
   * @throws DataHandlerException
   */
  public void onDisconnect(final String uci) throws
      DataHandlerException {
    super.onDisconnect(uci);
  }

  /**
   * Assigns data to only one assignable.
   *
   * @param uci unique connection identifier.
   * @param data
   * @throws DataHandlerException
   */
  public void onIncomingData(final String uci, final byte[] data) throws
      DataHandlerException {
    super.onIncomingData(uci, data);
  }

}

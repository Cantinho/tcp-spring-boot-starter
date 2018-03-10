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

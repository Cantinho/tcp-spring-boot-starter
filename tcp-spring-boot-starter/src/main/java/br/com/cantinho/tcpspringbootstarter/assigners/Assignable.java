package br.com.cantinho.tcpspringbootstarter.assigners;

import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import java.util.List;

/**
 * Assignable is a participant in charge of performing some task.
 */
public abstract class Assignable {

  /**
   * Retrieves the assignable name.
   *
   * @return
   */
  public abstract String getName();

  /**
   * Retrieves the versions of tasks understood by the participant.
   *
   * @return
   */
  public abstract List<String> getVersions();

  /**
   * Parses data message according to a specification understood by the participant.
   *
   * @param data
   * @return
   * @throws DataHandlerException
   */
  public abstract Object parse(final byte[] data) throws DataHandlerException;

  /**
   * Assigns message to participant.
   *
   * @param parameters
   */
  public abstract void assign(Object... parameters);

  /**
   * Verifies if assignable can perform his task asynchronously through the network.
   * @return
   */
  public abstract boolean isAddressable();

  /**
   * Retrieves the address.
   *
   * @return null if assignable wasn't an addressable.
   */
  public abstract Object getAddress();

}

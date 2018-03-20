package br.com.cantinho.tcpspringbootstarter.assigners;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
import br.com.cantinho.tcpspringbootstarter.clients.Transmitter;
import br.com.cantinho.tcpspringbootstarter.data.DataHandler;
import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Assignable is a participant in charge of performing some task.
 */
public abstract class Assignable {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Assignable.class.getCanonicalName());

  /**
   * Clients.
   */
  private final Transmitter transmitter;
  
  /**
   * Connected clients UCI.
   */
  private final Set<String> connectedClients = Collections.synchronizedSet(new HashSet<>());

  public Assignable(final Transmitter transmitter) throws
      AssignableException {
    if(null == transmitter) {
      throw new AssignableException("Transmitter can't be null.");
    }
    this.transmitter = transmitter;
  }

  public void send(final String uci, final Object... parameters) throws AssignableException {
    synchronized (connectedClients) {
      if (connectedClients.contains(uci)) {
        transmitter.send(uci, parameters);
      } else {
        throw new AssignableException("Couldn't find connected client with the given UCI " + uci);
      }
    }
  }

  public void close(final String uci, final Object... parameters) {
    synchronized (connectedClients) {
      connectedClients.remove(uci);
      transmitter.close(uci, parameters);
    }
  }

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

  public void onConnect(final String uci) {
    LOGGER.trace("onConnect::uci:{}", uci);
    connectedClients.add(uci);
  }

  public void onDisconnect(final String uci) {
    boolean success = connectedClients.remove(uci);
    LOGGER.trace("onDisconnect::uci:{}:status:{}", uci, success);
    connectedClients.remove(uci);
  }

  /**
   * Assigns message to participant.
   *
   * @param parameters
   */
  public void assign(Object... parameters) {
    LOGGER.trace("assign");
  }

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

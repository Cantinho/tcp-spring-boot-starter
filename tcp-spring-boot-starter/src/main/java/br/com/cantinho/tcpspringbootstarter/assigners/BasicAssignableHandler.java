package br.com.cantinho.tcpspringbootstarter.assigners;

import java.util.List;

/**
 * A basic assignable handler.
 */
public class BasicAssignableHandler extends AssignableHandler {

  public BasicAssignableHandler(final List<Assignable> assignables) throws AssignableHandlerException {
    super(assignables);
  }

  /**
   * Retrieve all assignagles.
   *
   * @return A list of assignables.
   */
  @Override
  public List<Assignable> getAssignables() {
    return super.getAssignables();
  }

}


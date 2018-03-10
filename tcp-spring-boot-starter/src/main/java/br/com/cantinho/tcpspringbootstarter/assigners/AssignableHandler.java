package br.com.cantinho.tcpspringbootstarter.assigners;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignable handler.
 */
public abstract class AssignableHandler {

  /**
   * Assignables registered.
   */
  private List<Assignable> assignables = new ArrayList<>();

  public AssignableHandler(final List<Assignable> assignables) throws AssignableHandlerException {
    if(null == assignables) {
      throw new AssignableHandlerException("No assignable handler was provided.");
    } else {
      assignables.clear();
      assignables.addAll(assignables);
    }
  }

  /**
   * Retrieve all assignagles.
   *
   * @return A list of assignables.
   */
  public List<Assignable> getAssignables() {
    return assignables;
  }


}

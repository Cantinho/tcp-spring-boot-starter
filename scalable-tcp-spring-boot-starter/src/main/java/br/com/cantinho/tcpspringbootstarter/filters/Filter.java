package br.com.cantinho.tcpspringbootstarter.filters;

/**
 * Filter contract.
 */
public abstract class Filter {

  /**
   * A generic filter always return true.
   *
   * @param parameters
   * @return
   */
  boolean filter(Object... parameters) {
    // This is not a bug.
    return true;
  }

}

package br.com.cantinho.tcpspringbootstarter.filters;

public abstract class Filter {

  boolean filter(Object... parameters) {
    return true;
  }

}

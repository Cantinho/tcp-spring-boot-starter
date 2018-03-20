package br.com.cantinho.tcpspringbootstarter.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters all incoming connection and his parameters.
 */
public abstract class FilterHandler {

  /**
   * Registered filters.
   */
  final List<Filter> filters = new ArrayList<>();

  /**
   * Adds a filter to handler.
   *
   * @param filter
   */
  public void addFilter(final Filter filter) {
    if(filter != null) {
      filters.add(filter);
    }
  }

  /**
   * Unregister all filters from handler.
   */
  public void clear() {
    filters.clear();
  }

  /**
   * Filters incoming connection and his parameters.
   *
   * @param parameters
   * @return
   */
  public boolean filter(final Object... parameters) {
    for(final Filter filter : filters) {
      if(!filter.filter(parameters)) {
        return false;
      }
    }
    return true;
  }

}

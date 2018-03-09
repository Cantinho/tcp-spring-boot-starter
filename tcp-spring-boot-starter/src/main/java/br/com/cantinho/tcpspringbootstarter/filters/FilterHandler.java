package br.com.cantinho.tcpspringbootstarter.filters;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterHandler {

  final List<Filter> filters = new ArrayList<>();

  public void addFilter(final Filter filter) {
    if(filter != null) {
      filters.add(filter);
    }
  }

  public void clear() {
    filters.clear();
  }

  public boolean filter(Object... parameters) {
    for(final Filter filter : filters) {
      if(!filter.filter(parameters)) {
        return false;
      }
    }
    return true;
  }

}

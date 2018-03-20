package br.com.cantinho.tcpspringbootstarter.filters;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a basic filter handler implementation.
 */
public class BasicFilterHandler extends FilterHandler {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BasicFilterHandler.class);

  /**
   * Builds a filter handler passing filters as argument.
   *
   * @param filters
   */
  public BasicFilterHandler(final List<Filter> filters) {
    if(null == filters) {
      LOGGER.warn("No filter was provided. All incoming events will be accepted " +
          "by filter handler.");
    } else {
      filters.clear();
      filters.addAll(filters);
    }
  }

  /**
   * Filters an incoming event.
   *
   * @param parameters
   * @return true if incoming event must be accepted; otherwise, false.
   */
  @Override
  public boolean filter(Object... parameters) {
    return super.filter(parameters);
  }
}

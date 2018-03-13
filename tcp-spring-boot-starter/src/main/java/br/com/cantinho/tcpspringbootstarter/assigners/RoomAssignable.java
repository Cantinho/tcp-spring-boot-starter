package br.com.cantinho.tcpspringbootstarter.assigners;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.*;
import br.com.cantinho.tcpspringbootstarter.clients.Transmitter;
import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static br.com.cantinho.tcpspringbootstarter.data.DataHandler.compareVersion;
import static br.com.cantinho.tcpspringbootstarter.data.DataHandler.getVersionable;

/**
 * Echos message to client.
 */
public class RoomAssignable extends Assignable {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoomAssignable.class.getCanonicalName());

  /**
   * Converters.
   */
  private List<IConverter> converters;

  /**
   * Builds an echo application passing a converter list as argument.
   *
   * @param converters
   * @throws AssignableException
   */
  public RoomAssignable(final List<IConverter> converters, final Transmitter transmitter, final
  Application application) throws
      AssignableException {
    super(transmitter, application);
    if(null == converters || converters.isEmpty()) {
      throw new AssignableException("It could not find a suitable converter.");
    }
    this.converters = converters;
  }

  /**
   * Retrieve assignable name.
   *
   * @return
   */
  @Override
  public String getName() {
    return RoomAssignable.class.getCanonicalName();
  }

  @Override
  public List<String> getVersions() {
    final List<String> versions = new ArrayList<>(converters.size());
    for(IConverter converter : converters) {
      versions.add(converter.version());
    }
    return versions;
  }

  @Override
  public Object parse(byte[] data) throws DataHandlerException {
    final Versionable versionable = getVersionable(data);
    for(final IConverter converter : converters) {
      final boolean equals = compareVersion(versionable.getVer(), converter.version());
      if(equals) {
        return converter.parse(data);
      }
    }
    throw new IllegalStateException("Unable to convert data. Fix this before production.");
  }

  @Override
  public void onDisconnect(final String uci) {
    super.onDisconnect(uci);
    application.onDisconnect(uci);
  }

  @Override
  public void assign(Object... parameters) {
    super.assign(parameters);
    if(null == parameters || parameters.length < 2) {
      throw new IllegalStateException("Unable to assign data. Fix this before production.");
    }
    final String uci = (String) parameters[0];
    final Object data = parameters[1];
    final Class clazz = data.getClass();

    final ObjectMapper mapper = new ObjectMapper();
    //Object to JSON in String
    try {

      final RoomData request = RoomDataConverter.jsonize(data);
      final RoomData response = (RoomData) application.process(request);
      final Object objectData = RoomDataConverter.dejsonizeFrom(clazz, response);
      final String jsonInString = mapper.writeValueAsString(objectData);

      // Transmitting the message back to client.
      try {
        send(uci, jsonInString.getBytes());
      } catch (AssignableException e) {
        LOGGER.debug("Content not sent. Message: {}", e.getMessage());
      }
    } catch (Exception e) {
      LOGGER.error("Couldn't assign. Message: {}", e.getMessage());
    }
  }

  @Override
  public boolean isAddressable() {
    return false;
  }

  @Override
  public Object getAddress() {
    return null;
  }

  public static void main(String[] args) {
    final String source = "from";
    final String destination = "to";
    final String payload = "data";
    V1Data data = new V1Data(source, destination, payload);

    Object object = data;
    System.out.println(object.getClass());

    ObjectMapper mapper = new ObjectMapper();
    //Object to JSON in String
    try {
      String jsonInString = mapper.writeValueAsString(data);
      System.out.println("json:{"+jsonInString+"]");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }
}

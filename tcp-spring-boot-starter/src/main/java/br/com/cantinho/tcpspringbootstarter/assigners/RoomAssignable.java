package br.com.cantinho.tcpspringbootstarter.assigners;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
import br.com.cantinho.tcpspringbootstarter.applications.EchoApplication;
import br.com.cantinho.tcpspringbootstarter.applications.RoomApplication;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.EchoData;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.EchoDataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.IConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomData;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomDataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.V1Data;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.Versionable;
import br.com.cantinho.tcpspringbootstarter.clients.Transmitter;
import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static br.com.cantinho.tcpspringbootstarter.data.DataHandler.compareVersion;
import static br.com.cantinho.tcpspringbootstarter.data.DataHandler.getVersionable;

/**
 * Echos message to client.
 */
public class RoomAssignable extends Assignable {

  private Application application = new RoomApplication();

  /**
   * Clients.
   */
  private Transmitter transmitter;

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
  public RoomAssignable(final List<IConverter> converters, final Transmitter transmitter) throws
      AssignableException {
    if(null == converters || converters.isEmpty()) {
      throw new AssignableException("It could not find a suitable converter.");
    }
    if(null == transmitter) {
      throw new AssignableException("It could not find a suitable transmitter.");
    }
    this.converters = converters;
    this.transmitter = transmitter;
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
  public void assign(Object... parameters) {
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

      RoomData response = (RoomData) application.process(request);

      Object objectData = RoomDataConverter.dejsonizeFrom(clazz, response);

      final String jsonInString = mapper.writeValueAsString(objectData);

      // Transmitting the message back to client.
      transmitter.send(uci, jsonInString.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
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

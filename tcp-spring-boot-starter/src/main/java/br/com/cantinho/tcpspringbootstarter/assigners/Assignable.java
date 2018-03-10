package br.com.cantinho.tcpspringbootstarter.assigners;

import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import java.util.List;

public abstract class Assignable {

  public abstract List<String> getVersions();

  public abstract Object parse(final byte[] data) throws DataHandlerException;

  public abstract void assign(Object... parameters);

  public abstract boolean isAddressable();

  public abstract Object getAddress();

}

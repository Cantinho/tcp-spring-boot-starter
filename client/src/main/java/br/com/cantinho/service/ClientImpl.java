package br.com.cantinho.service;

import br.com.cantinho.domain.RoomV1Data;
import br.com.cantinho.domain.RoomV2Data;
import br.com.cantinho.domain.SimpleMessage;
import br.com.cantinho.domain.V1Data;
import br.com.cantinho.domain.V2Data;
import br.com.cantinho.utils.Utils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

@Component
public class ClientImpl implements Client {

  /**
   * A logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientImpl.class.getCanonicalName());

  /**
   * A constant identifying buffer reader size.
   */
  private static final int BUFFER_SIZE = 64*1024;

  /**
   * A string representing server hostname.
   */
  @Value("${server.host}")
  private String host;

  /**
   * A integer representing server port number.
   */
  @Value("${server.port}")
  private int port;

  /**
   * A integer representing secure server port number.
   */
  @Value("${server.secure.port}")
  private int securePort;

  /**
   * A boolean true if communication if secure and false otherwise.
   */
  @Value("${client.secure.communication}")
  private boolean isSecureCommunication;

  /**
   * A string representing keystore file name.
   */
  @Value("${keystore.file.name}")
  private String keystore;

  /**
   * A string representing keystore pass.
   */
  @Value("${keystore.pass}")
  private String pass;

  /**
   * A start point for client.
   */
  private void init() {
    if(isSecureCommunication) {
      runSecureCommunication();
    } else {
      runInsecureCommunication();
    }
  }

  /**
   * Listens for user inputs to send to a server in a secure socket communication.
   */
  private void runSecureCommunication(){
    try {
      final SSLSocket sslSocket = getSSLSocket();
      final OutputStream outputStream = sslSocket.getOutputStream();
      final InputStream inputStream = sslSocket.getInputStream();
      sslSocket.startHandshake();

      new Thread(new Runnable() {
        @Override
        public void run() {
          final String client = "client " + new Random().nextInt();
          //sslSocket.getLocalAddress().getHostName() + ":" + sslSocket.getLocalPort();
          while (true == true) {
            try {
              System.out.print("[client] data version (v1, v2): ");
              final String data = new Scanner(System.in).nextLine();
              System.out.print("[client] message to send: ");
              final String message = new Scanner(System.in).nextLine();

              Object obj = null;
              switch (data) {
                case "v1":
                  obj = new RoomV1Data(client, "broadcast", message);
                  break;
                case "v2":
                  obj = new RoomV2Data(client, "broadcast", message);
                  break;
              }

              outputStream.write(new Gson().toJson(obj).getBytes());
              outputStream.flush();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }).start();

      new Thread(new Runnable() {
        @Override
        public void run() {
          final byte[] buffer = new byte[BUFFER_SIZE];
          int size = 0;
          try{
            while ((size = inputStream.read(buffer)) >= 0) {
              final byte[] response = Arrays.copyOfRange(buffer, 0, size);
              LOGGER.info("[client]: {}", new String(response));
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();

    } catch (Exception exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
    }
  }

  /**
   * Listens for user inputs to send to a server in a insecure socket communication.
   */
  private void runInsecureCommunication(){
    try (
        final Socket socket = new Socket(host, port);
        final OutputStream outputStream = socket.getOutputStream();
        final InputStream inputStream = socket.getInputStream()
    ) {
      final byte[] buffer = new byte[BUFFER_SIZE];

      while(true) {
        final String line = new Scanner(System.in).nextLine();
        SimpleMessage messageMapper = Utils.createMessage(socket, line);

        outputStream.write(Utils.getBytesFromMessage(messageMapper));
        outputStream.flush();

        final int size = inputStream.read(buffer);
        if(size < 0) {
          LOGGER.warn("[client]: no bytes to read");
          break;
        }
        final byte[] response = Arrays.copyOfRange(buffer, 0, size);
        SimpleMessage responseMessage = Utils.getMessageFromBytes(response);
        LOGGER.info("[client]: {}", responseMessage);
      }

    } catch (Exception exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
    }
  }

  /**
   * Creates a socket for secure communication using provided keystore and credentials.
   *
   * @return a secure socket.
   * @throws Exception an Exception can be thrown if ssl socket cannot be created.
   */
  private SSLSocket getSSLSocket() throws Exception {
    try {
      // creating a KeyStore containing our trusted CAs
      final KeyStore keyStore = KeyStore.getInstance("PKCS12");
      final InputStream keystoreInputStream = new ClassPathResource(keystore).getInputStream();
      keyStore.load(keystoreInputStream, pass.toCharArray());
      // creating a TrustManager that trusts the CAs in our KeyStore
      final String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
      tmf.init(keyStore);

      // creating an SSLSocketFactory that uses our TrustManager
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, tmf.getTrustManagers(), null);
      final SSLSocketFactory socketFactory = sslContext.getSocketFactory();

      if(null != socketFactory) {
        return (SSLSocket) socketFactory.createSocket(host, securePort);
      }
    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
        KeyManagementException | IOException exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
      throw new Exception("Could not establish secure communication.", exc);
    }
    throw new Exception("Could not establish secure communication.");
  }

  @Override
  public void start() {
    new Thread(this::init).start();
  }
}
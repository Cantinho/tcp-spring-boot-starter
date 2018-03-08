package br.com.cantinho.service;

import br.com.cantinho.utils.Utils;
import br.com.cantinho.domain.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
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

@Component
public class ServerImpl implements Server {

  /**
   * A logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerImpl.class.getCanonicalName());

  /**
   * A constant identifying buffer reader size.
   */
  private static final int BUFFER_SIZE = 64*1024;

  /**
   * A integer representing secure server port number.
   */
  @Value("${server.secure.port}")
  private int securePort;

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
   * A start point for server.
   */
  private void init() {
    try {
      runServer();
    } catch (Exception exc) {
      LOGGER.error("[error]: {}", exc.getMessage());
    }
  }

  /**
   * Starts listening to incoming connection on secure server socket.
   * @throws Exception
   */
  public void runServer() throws Exception {
    LOGGER.info("[server]: listening on port {}", securePort);
    final SSLServerSocket sslServerSocket = getSSLServerSocket();

    ((Runnable)() -> {
      final Socket clientSocket;
      try {
        clientSocket = sslServerSocket.accept();
        LOGGER.info("[server]: connection from {}", clientSocket.getRemoteSocketAddress());

        try (
            final InputStream inputStream = clientSocket.getInputStream();
            final OutputStream outputStream = clientSocket.getOutputStream()
        ) {
          while (true) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            final int size = inputStream.read(buffer);
            if (size < 0) {
              LOGGER.warn("[server]: no bytes to read");
              break;
            }
            final byte[] response = Arrays.copyOfRange(buffer, 0, size);
            SimpleMessage responseMapper = Utils.getMessageFromBytes(response);
            LOGGER.info("[server]: {}", responseMapper);

            responseMapper = Utils.exchangeFromTo(responseMapper);

            // send data back
            outputStream.write(Utils.getBytesFromMessage(responseMapper));
            outputStream.flush();
          }
        } catch (Exception exc) {
          LOGGER.error("[error]: {}", exc.getMessage());
        }
      } catch (IOException e) {
        LOGGER.error("[error]: {}", e.getMessage());
      }

    }).run();
  }

  /**
   * Creates a server socket for secure communication using provided keystore and credentials.
   *
   * @return a secure server socket.
   * @throws Exception an Exception can be thrown if ssl server socket cannot be created.
   */
  private SSLServerSocket getSSLServerSocket() throws Exception {
    try {
      final KeyStore keyStore = KeyStore.getInstance("PKCS12");
      final InputStream keystoreInputStream = new ClassPathResource(keystore).getInputStream();
      keyStore.load(keystoreInputStream, pass.toCharArray());

      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, pass.toCharArray());

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), null, null);
      final SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
      if(null != socketFactory) {
        return (SSLServerSocket) socketFactory.createServerSocket(securePort);
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

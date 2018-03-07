package br.com.cantinho.tcpspringbootstarter;

/*
 *
 * Copyright (c) 1994, 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistribution of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistribution in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import org.springframework.core.io.ClassPathResource;

import java.net.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.net.ssl.*;

import static javafx.scene.input.KeyCode.R;

/*
 * This example demostrates how to use a SSLSocket as client to
 * send a HTTP request and get response from an HTTPS server.
 * It assumes that the client is not behind a firewall
 */

public class SSLSocketClient {

  /**
   * Configures sslSocketFactory being used by retrofit to retrieve credentials from keystore.
   *
   * @return a SSLSocketFactory configured with right credentials.
   */
  private static SSLSocketFactory getSSLSocketFactory() {
    // loading CAs from an InputStream
    CertificateFactory cf = null;
    try {
      // creating a KeyStore containing our trusted CAs
      String keyStoreType = KeyStore.getDefaultType();
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      InputStream keystoreInputStream = new ClassPathResource("keystore").getInputStream();;
      keyStore.load(keystoreInputStream, "keystorecredentials".toCharArray());
      // creating a TrustManager that trusts the CAs in our KeyStore
      String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
      tmf.init(keyStore);

      // creating an SSLSocketFactory that uses our TrustManager
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, tmf.getTrustManagers(), null);
      return sslContext.getSocketFactory();
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void main(String[] args) throws Exception {

    SSLSocketFactory factory = getSSLSocketFactory();
    try(
        SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8081)) {

      /*
       * send http request
       *
       * Before any application data is sent or received, the
       * SSL socket will do SSL handshaking first to set up
       * the security attributes.
       *
       * SSL handshaking can be initiated by either flushing data
       * down the pipe, or by starting the handshaking by hand.
       *
       * Handshaking is started manually in this example because
       * PrintWriter catches all IOExceptions (including
       * SSLExceptions), sets an internal error flag, and then
       * returns without rethrowing the exception.
       *
       * Unfortunately, this means any error messages are lost,
       * which caused lots of confusion for others using this
       * code.  The only way to tell there was an error is to call
       * PrintWriter.checkError().
       */
      socket.startHandshake();

      PrintWriter out = new PrintWriter(
          new BufferedWriter(
              new OutputStreamWriter(
                  socket.getOutputStream())));

      out.println("GET / HTTP/1.0");
      out.println();
      out.flush();

      /*
       * Make sure there were no surprises
       */
      if (out.checkError())
        System.out.println(
            "SSLSocketClient:  java.io.PrintWriter error");

      /* read response */
      BufferedReader in = new BufferedReader(
          new InputStreamReader(
              socket.getInputStream()));

      String inputLine;
      while ((inputLine = in.readLine()) != null)
        System.out.println(inputLine);

      in.close();
      out.close();


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
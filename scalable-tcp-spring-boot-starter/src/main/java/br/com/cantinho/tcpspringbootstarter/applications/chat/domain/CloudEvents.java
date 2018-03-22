package br.com.cantinho.tcpspringbootstarter.applications.chat.domain;

public class CloudEvents {

  /**
   * Create Room (CREATE_ROOM)
   * Utilizado por um usuário para criar uma sala de conversação. Outros usuário poderão se juntar
   * a esta sala.
   */
  public static final String CREATE_ROOM = "CREATE_ROOM";
  
  /**
   * Leave Room (LEAVE_ROOM)
   * Utlizado por um usuário já conectado e dentro de uma sala de conversação para sair da mesma.
   */
  public static final String LEAVE_ROOM = "LEAVE_ROOM";

  /**
   * Leave Room (LEAVE_ROOM_OWNER)
   * Utlizado por um criador de sala já conectado e dentro de uma sala de conversação para sair da
   * mesma.
   */
  public static final String LEAVE_ROOM_OWNER = "LEAVE_ROOM_OWNER";

  /**
   * Send Message To Specific User in a Room (SEND_SUR)
   * Envia mensagem para um usuário específico na mesma sala a qual você pertence.
   */
  public static final String SEND_SUR = "SEND_SUR";

}

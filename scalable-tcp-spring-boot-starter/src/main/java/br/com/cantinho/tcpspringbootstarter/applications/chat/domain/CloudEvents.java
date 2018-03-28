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

  /**
   * Send Broadcast Message to Users in a Room (SEND_BUR)
   * Envia mensagem para uma sala e todos os usuários dentro da mesma receberão a mensagem, com
   * excessão do usuário que enviou a mensagem.
   */
  public static final String SEND_BUR = "SEND_BUR";

  /**
   * Send Broadcast Message to Specific Global User (SEND_SGU)
   * Envia mensagem para um usuário específico que pode estar conectado no servidor.
   */
  public static final String SEND_SGU = "SEND_SGU";

  /**
   * Send Broadcast Message to All Global Users (SEND_BGU)
   * Envia mensagem para todos os usuários conectados no servidor.
   */
  public static final String SEND_BGU = "SEND_BGU";

}

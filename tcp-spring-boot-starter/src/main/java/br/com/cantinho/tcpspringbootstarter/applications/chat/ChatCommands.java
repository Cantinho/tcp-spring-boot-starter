package br.com.cantinho.tcpspringbootstarter.applications.chat;

public class ChatCommands {
  /**
   * Connect (CONNECT)
   * Comando utilizado para conectar um usuário ao servidor. 
   */
  public static final String CONNECT = "CONNECT";

  /**
   * Disconnect (DISCONNECT)
   * Comando utilizado para desconectar um usuário do servidor. O disconnect deve ser realizado 
   * automaticamente nos casos em que o socket seja fechado.
   */
  public static final String DISCONNECT = "DISCONNECT";

  /**
   * Create Room (CREATE_ROOM)
   * Utilizado por um usuário para criar uma sala de conversação. Outros usuário poderão se juntar
   * a esta sala.
   */
  public static final String CREATE_ROOM = "CREATE_ROOM";
  
  /**
   * Join Room (JOIN_ROOM)
   * Utilizado por um usuário para entrar em uma sala de conversação. Uma vez dentro da sala, o 
   * usuário pode realizar comunicação com outros usuários.
   */
  public static final String JOIN_ROOM = "JOIN_ROOM";

  /**
   * Leave Room (LEAVE_ROOM)
   * Utlizado por um usuário já conectado e dentro de uma sala de conversação para sair da mesma.
   */
  public static final String LEAVE_ROOM = "LEAVE_ROOM";
  
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
  
  /**
   * Retrieve All Users IDs in a Room (USERS_ROOM)
   * Recupera a lista de usuários de uma sala.
   */
  public static final String USERS_ROOM = "USERS_ROOM";
  
  /**
   * Keep Alive (KEEP_ALIVE)
   * Envia um comando para o servirdor apenas para notificar ao mesmo que o usuário ainda está 
   * ativo.
   */
  public static final String KEEP_ALIVE = "KEEP_ALIVE";
}

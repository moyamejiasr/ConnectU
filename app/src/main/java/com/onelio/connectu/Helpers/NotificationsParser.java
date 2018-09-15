package com.onelio.connectu.Helpers;

public class NotificationsParser {

  public static String getName(String current) {
    if (current.equals("UAMENSAJES")) return "Mensajes";
    if (current.equals("MATDOCENTE")) return "Materiales";
    if (current.equals("UATUTORIAS")) return "Tutorias";
    if (current.equals("CUESTIONARIO")) return "Cuestionario";
    if (current.equals("UAEVALUACION")) return "Evaluación";
    if (current.equals("ANUNCIOS")) return "Anuncios";
    return "";
  }
}

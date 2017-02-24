package com.onelio.connectu.Apps.Anuncios;

/**
 * Created by Onelio on 02/12/2016.
 */
public class AdList {
    private String badge;
    private String titulo1;
    private String titulo2;
    private String fecha;
    private String htexto;
    private String texto;
    private String asignatura;
    private String profesor;
    private String id;
    private boolean state;

    public String getBadge() {
        return badge;
    }
    public void setBadge(String text) {
        this.badge = text;
    }
    public String getTitulo1() {
        return titulo1;
    }
    public void setTitulo1(String text) {
        this.titulo1 = text;
    }

    public String getTitulo2() {
        return titulo2;
    }
    public void setTitul2(String text) {
        this.titulo2 = text;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String text) {
        this.fecha = text;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String text) {
        this.texto = text;
    }

    public String getAsignatura() {
        return asignatura;
    }
    public void setAsignatura(String text) {
        this.asignatura = text;
    }

    public String getProfesor() {
        return profesor;
    }
    public void setProfesor(String text) {
        this.profesor = text;
    }

    public String getId() {
        return id;
    }
    public void setId(String text) {
        this.id = text;
    }

    public boolean getState() {
        return state;
    }
    public void setState(boolean text) {
        this.state = text;
    }

    public String getHTexto() {
        return htexto;
    }
    public void setHTexto(String text) {
        this.htexto = text;
    }

}

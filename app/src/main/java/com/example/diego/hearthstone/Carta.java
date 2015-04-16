package com.example.diego.hearthstone;

/**
 * Created by blukstack on 07/04/2015.
 */
public class Carta {
    private int id;
    private String nombre;
    private String clase;
    private boolean obtenida;
    private String url;
    private String tipo;
    private int coste;
    private int cantidad;

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {

        return cantidad;
    }

    public Carta(){

    }

    public Carta(int id, String nombre, String clase, boolean obtenida, String url, String tipo, int coste) {
        this.id = id;
        this.nombre = nombre;
        this.clase = clase;
        this.obtenida = obtenida;
        this.url = url;
        this.tipo = tipo;
        this.coste = coste;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public void setObtenida(boolean obtenida) {
        this.obtenida = obtenida;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCoste(int coste) {
        this.coste = coste;
    }

    public Carta(int id, String nombre, String clase, String url, String tipo, int coste) {
        this.id = id;
        this.nombre = nombre;
        this.clase = clase;
        this.url = url;
        this.tipo = tipo;
        this.coste = coste;
    }

    public int getId() {

        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getClase() {
        return clase;
    }

    public boolean isObtenida() {
        return obtenida;
    }

    public String getUrl() {
        return url;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCoste() {
        return coste;
    }
}

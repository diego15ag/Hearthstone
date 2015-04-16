package com.example.diego.hearthstone;

/**
 * Created by blukstack on 07/04/2015.
 */
public class Mazo {
    private int id;
    private String nombre;
    private boolean predefinido;
    private String clase;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isPredefinido() {
        return predefinido;
    }

    public String getClase() {
        return clase;
    }

    public Mazo(int id, String nombre, boolean predefinido, String clase) {
        this.id = id;
        this.nombre = nombre;
        this.predefinido = predefinido;
        this.clase = clase;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPredefinido(boolean predefinido) {
        this.predefinido = predefinido;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }
}

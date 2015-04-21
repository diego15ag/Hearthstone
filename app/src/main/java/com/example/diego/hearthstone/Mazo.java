package com.example.diego.hearthstone;

import java.util.ArrayList;

/**
 * Created by blukstack on 07/04/2015.
 */
public class Mazo {
    private int id;
    private String nombre;
    private boolean predefinido;
    private String clase;
    private ArrayList<Carta> cartas;


    public Mazo() {
    }

    public Mazo(int id, String nombre, boolean predefinido, String clase, ArrayList<Carta> cartas) {
        this.id = id;
        this.nombre = nombre;
        this.predefinido = predefinido;
        this.clase = clase;
        this.cartas = cartas;
    }

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

    public ArrayList<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(ArrayList<Carta> cartas) {
        this.cartas = cartas;
    }
}

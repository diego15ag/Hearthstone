package com.example.diego.hearthstone;

import java.io.Serializable;

/**
 * Created by blukstack on 07/04/2015.
 */
public class Carta implements Serializable{
    private int id;
    private String nombre;
    private String clase;
    private boolean obtenida;
    private String url;
    private String tipo;
    private int coste;
    private int cantidad;
    private int pesos[] = {0,0,0,0,0,0,0,0,0};
    private String conjunto;

    public String getConjunto() {
        return conjunto;
    }

    public void setConjunto(String conjunto) {
        this.conjunto = conjunto;
    }

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

    public void setPesos(int[] newpesos){
        for(int i=0;i<pesos.length;i++){
            pesos[i]=newpesos[i];
        }
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

    public int getPeso(int heroe){
        if(heroe<pesos.length)
            return pesos[heroe];
        else
            return -1;
    }

    public Carta clone(){
        Carta c =new Carta(this.id, this.nombre, this.clase, this.url, this.tipo, this.coste);
        c.setCantidad(this.cantidad);
        c.setConjunto(this.conjunto);
        return c;
    }
}

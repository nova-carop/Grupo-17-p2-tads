package entidades;

import Tads.ListaEnlazada;

public class Saga implements Comparable<Saga> {
    private int id;
    private String titulo;
    private int cantidad_peliculas;
    private int ingreso_generado;

    public Saga(int id, String titulo, int cantidad_peliculas, int ingreso_generado) {
        this.id = id;
        this.titulo = titulo;
        this.cantidad_peliculas = cantidad_peliculas;
        this.ingreso_generado = ingreso_generado;
    }

    public Saga() {
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCantidad_peliculas() {
        return cantidad_peliculas;
    }

    public void setCantidad_peliculas(int cantidad_peliculas) {
        this.cantidad_peliculas = cantidad_peliculas;
    }

    public int getIngreso_generado() {
        return ingreso_generado;
    }

    public void setIngreso_generado(int ingreso_generado) {
        this.ingreso_generado = ingreso_generado;
    }


    public int compareTo(Saga otra) {
        return Integer.compare(this.ingreso_generado, otra.ingreso_generado); // min-heap
    }



}

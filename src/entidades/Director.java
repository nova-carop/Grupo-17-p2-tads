package entidades;

import Tads.ListaEnlazada;

import java.util.LinkedList;

public class Director {
    private String nombre;
    private int cantidad_pelis;
    private float mediana_calificacion;
    private ListaEnlazada<Pelicula> peliculas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad_pelis() {
        return cantidad_pelis;
    }

    public void setCantidad_pelis(int cantidad_pelis) {
        this.cantidad_pelis = cantidad_pelis;
    }

    public float getMediana_calificacion() {
        return mediana_calificacion;
    }

    public void setMediana_calificacion(float mediana_calificacion) {
        this.mediana_calificacion = mediana_calificacion;
    }

    public ListaEnlazada<Pelicula> getPeliculasDirigidas() {
        return peliculas;
    }

    public Director(String nombre, int cantidad_pelis, float mediana_calificacion, ListaEnlazada<Pelicula> peliculas) {
        this.nombre = nombre;
        this.cantidad_pelis = cantidad_pelis;
        this.mediana_calificacion = mediana_calificacion;
        this.peliculas = peliculas;
    }

    public Director() {
    }
}
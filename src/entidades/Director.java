package entidades;

import java.util.LinkedList;

public class Director {
    private String nombre;
    private int cantidad_pelis;
    private float mediana_calificacion;
    private LinkedList<Pelicula> peliculas;

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
}

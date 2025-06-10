package entidades;

import java.util.LinkedList;

public class Actor {
    private String nombre;
    private int puntaje;
    private LinkedList<Pelicula> peliculas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}

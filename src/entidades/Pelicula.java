package entidades;

import java.util.Date;
import java.util.LinkedList;

public class Pelicula {
    private int id;
    private String titulo;
    private String idioma_original;
    private float calificacion_media;
    private Date fecha_publicacion;
    private int id_saga;
    private LinkedList<Actor> actores;
    private LinkedList<Review> review;

    //FALTA LA LISTA DE TOTAL DE EVALUACIONES

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

    public String getIdioma_original() {
        return idioma_original;
    }

    public void setIdioma_original(String idioma_original) {
        this.idioma_original = idioma_original;
    }

    public float getCalificacion_media() {
        return calificacion_media;
    }

    public void setCalificacion_media(float calificacion_media) {
        this.calificacion_media = calificacion_media;
    }

    public Date getFecha_publicacion() {
        return fecha_publicacion;
    }

    public void setFecha_publicacion(Date fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
    }
}

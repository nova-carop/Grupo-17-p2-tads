package entidades;

import Tads.ListaEnlazada;

import java.util.Date;

public class Pelicula {
    private int id;
    private String titulo;
    private String idioma_original;
    private float calificacion_media;
    private Date fecha_publicacion;
    private int id_saga;
    private ListaEnlazada<Actor> actores;
    private ListaEnlazada<Review> review;


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


    public ListaEnlazada<Actor> getActores() {
        return actores;
    }

    public int getId_saga() {
        return id_saga;
    }

    public void setId_saga(int id_saga) {
        this.id_saga = id_saga;
    }

    public void setActores(ListaEnlazada<Actor> actores) {
        this.actores = actores;
    }

    public ListaEnlazada<Review> getReview() {
        return review;
    }

    public void setReview(ListaEnlazada<Review> review) {
        this.review = review;
    }

    public Pelicula(int id, String titulo, String idioma_original, float calificacion_media, Date fecha_publicacion) {
        this.id = id;
        this.titulo = titulo;
        this.idioma_original = idioma_original;
        this.calificacion_media = calificacion_media;
        this.fecha_publicacion = fecha_publicacion;
    }

    public Pelicula(int id, String titulo, String idioma_original, float calificacion_media, Date fecha_publicacion, int id_saga, ListaEnlazada<Actor> actores, ListaEnlazada<Review> review) {
        this.id = id;
        this.titulo = titulo;
        this.idioma_original = idioma_original;
        this.calificacion_media = calificacion_media;
        this.fecha_publicacion = fecha_publicacion;
        this.id_saga = id_saga;
        this.actores = actores;
        this.review = review;
    }
}
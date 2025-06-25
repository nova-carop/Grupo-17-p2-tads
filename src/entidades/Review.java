package entidades;

public class Review implements Comparable<Review> {
    private int id;
    private float calificacion;
    private int id_pelicula;
    private int id_usuario;

    public Review(int id, int id_usuario, int id_pelicula, float calificacion) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.id_pelicula = id_pelicula;
        this.calificacion = calificacion;
    }

    public Review() {

    }

    public int getId() {
        return id;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public int getId_pelicula() {
        return id_pelicula;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public void setId_pelicula(int id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    @Override
    public int compareTo(Review otra) {
        // Mayor calificación = mayor prioridad
        // Si calificación es igual, menor id = mayor prioridad (llegó antes)
        if (this.calificacion != otra.calificacion) {
            return Float.compare(otra.calificacion, this.calificacion); // descendente
        } else {
            return Integer.compare(this.id, otra.id); // ascendente por orden de llegada
        }
    }
}


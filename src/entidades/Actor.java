package entidades;

public class Actor {
    private String nombre;
    private int puntaje;

    public Actor(String name) {
    }

    public Actor(String nombre, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

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

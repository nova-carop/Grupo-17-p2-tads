package entidades;

public class Saga {
    private int id;
    private String titulo;
    private int cantidad_peliculas;
    private int ingreso_generado;

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
}

import entidades.Pelicula;
import entidades.Review;
import Tads.ListaEnlazada;
import Tads.TablaHash;
import interfaces.UMovieMgt;

import java.util.Arrays;

public class UMovieimpl implements UMovieMgt {
    private CargaDeDatos cargaDeDatos;

    public UMovieimpl(CargaDeDatos cargaDeDatos) {
        this.cargaDeDatos = cargaDeDatos;
    }

    @Override

    public void Top_5_de_las_películas_que_más_calificaciones_por_idioma() {
        long startTime = System.currentTimeMillis();

        // Idiomas objetivo
        String[] idiomasObjetivo = {"en", "fr", "it", "es", "pt"};


        TablaHash<Integer, Pelicula> tablaPeliculas = cargaDeDatos.getTablaPeliculas();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = cargaDeDatos.getReviewsPorPelicula();

        System.out.println("\nTop 5 de películas con más evaluaciones por idioma:");
        System.out.println("ID     Título                                      Evaluaciones  Idioma");
        System.out.println("-------------------------------------------------------------------------");

        // Procesar cada idioma
        for (String idioma : idiomasObjetivo) {
            System.out.println("\nIdioma: " + idioma);

            // Crear una lista temporal para las películas de este idioma
            ListaEnlazada<Pelicula> peliculasIdioma = new ListaEnlazada<>();

            // recolectar todas las películas del idioma actual que tienen reviews
            for (Integer idPelicula : reviewsPorPelicula.claves()) {
                Pelicula pelicula = tablaPeliculas.get(idPelicula);
                if (pelicula != null && idioma.equals(pelicula.getIdioma_original())) {
                    peliculasIdioma.insertar(pelicula);
                }
            }

            // ordenar las películas por cantidad de reviews (descendente)
            ordenarPeliculasPorReviews(peliculasIdioma, reviewsPorPelicula);

            //  mostrar las top 5
            int contador = 0;
            ListaEnlazada.Nodo<Pelicula> actual = peliculasIdioma.getCabeza();
            while (actual != null && contador < 5) {
                Pelicula pelicula = actual.getDato();
                ListaEnlazada<Review> reviews = reviewsPorPelicula.get(pelicula.getId());
                int totalEvaluaciones = contarReviews(reviews);

                String titulo = pelicula.getTitulo();
                if (titulo.length() > 40) {
                    titulo = titulo.substring(0, 37) + "...";
                }

                System.out.printf("%-6d %-40s %,14d %7s%n",
                        pelicula.getId(),
                        titulo,
                        totalEvaluaciones,
                        pelicula.getIdioma_original());

                contador++;
                actual = actual.getSiguiente();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
    }

    // Método auxiliar para contar reviews de una película
    private int contarReviews(ListaEnlazada<Review> reviews) {
        if (reviews == null) return 0;

        int contador = 0;
        ListaEnlazada.Nodo<Review> actual = reviews.getCabeza();
        while (actual != null) {
            contador++;
            actual = actual.getSiguiente();
        }
        return contador;
    }

    // Método para ordenar películas por cantidad de reviews (descendente)
    private void ordenarPeliculasPorReviews(ListaEnlazada<Pelicula> peliculas,
                                            TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula) {
        if (peliculas.estaVacia() || peliculas.tamanio() == 1) {
            return;
        }

        boolean intercambiado;
        do {
            intercambiado = false;
            ListaEnlazada.Nodo<Pelicula> anterior = null;
            ListaEnlazada.Nodo<Pelicula> actual = peliculas.getCabeza();
            ListaEnlazada.Nodo<Pelicula> siguiente = actual != null ? actual.getSiguiente() : null;

            while (siguiente != null) {
                // Obtener cantidad de reviews para cada película
                int reviewsActual = contarReviews(reviewsPorPelicula.get(actual.getDato().getId()));
                int reviewsSiguiente = contarReviews(reviewsPorPelicula.get(siguiente.getDato().getId()));

                if (reviewsActual < reviewsSiguiente) {
                    // Intercambiar nodos
                    actual.setSiguiente(siguiente.getSiguiente());
                    siguiente.setSiguiente(actual);

                    if (anterior == null) {
                        peliculas.setCabeza(siguiente);
                    } else {
                        anterior.setSiguiente(siguiente);
                    }

                    // Actualizar referencia para continuar
                    ListaEnlazada.Nodo<Pelicula> temp = actual;
                    actual = siguiente;
                    siguiente = temp;

                    intercambiado = true;
                }

                // Avanzar al siguiente nodo
                anterior = actual;
                actual = siguiente;
                if (siguiente != null) {
                    siguiente = siguiente.getSiguiente();
                }
            }
        } while (intercambiado);
    }



    @Override
    public void Top_10_de_las_películas_que_mejor_calificación_media_tienen_por_parte_de_los_usuarios() {

    }

    @Override
    public void Top_5_de_las_colecciones_que_más_ingresos_generaron() {




        System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Top_10_de_los_directores_que_mejor_calificación_tienen() {
        System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año() {
        System.out.println("<mes>,<nombre_actor>,<cantidad_peliculas>,<cantidad_de_calificaciones>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Usuarios_con_más_calificaciones_por_género() {
        System.out.println("<id_usuario>,<genero>,<cantidad_de_calificaciones_sobre_ese_genero>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }
}

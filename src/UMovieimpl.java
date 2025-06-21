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

        // Obtener todas las películas y sus reviews
        TablaHash<Integer, Pelicula> tablaPeliculas = cargaDeDatos.getTablaPeliculas();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = cargaDeDatos.getReviewsPorPelicula();

        // Crear una tabla hash para agrupar películas por idioma
        TablaHash<String, ListaEnlazada<PeliculaConteo>> peliculasPorIdioma = new TablaHash<>();

        // Inicializar listas para cada idioma
        for (String idioma : idiomasObjetivo) {
            peliculasPorIdioma.put(idioma, new ListaEnlazada<>());
        }

        // Recorrer todas las películas con reviews
        for (Integer id : reviewsPorPelicula.claves()) {
            Pelicula pelicula = tablaPeliculas.get(id);
            if (pelicula != null) {
                String idioma = pelicula.getIdioma_original();
                ListaEnlazada<Review> reviews = reviewsPorPelicula.get(id);

                if (reviews != null && Arrays.asList(idiomasObjetivo).contains(idioma)) {
                    int totalEvaluaciones = 0;
                    ListaEnlazada.Nodo<Review> actual = reviews.getCabeza();
                    while (actual != null) {
                        totalEvaluaciones++;
                        actual = actual.getSiguiente();
                    }

                    PeliculaConteo pc = new PeliculaConteo(pelicula, totalEvaluaciones);

                    ListaEnlazada<PeliculaConteo> listaIdioma = peliculasPorIdioma.get(idioma);
                    if (listaIdioma != null) {
                        listaIdioma.insertar(pc);
                    }
                }
            }
        }


        System.out.println("\nTop 5 de películas con más evaluaciones por idioma:");
        System.out.println("ID     Título                                      Evaluaciones  Idioma");
        System.out.println("-------------------------------------------------------------------------");

        for (String idioma : idiomasObjetivo) {


            ListaEnlazada<PeliculaConteo> peliculasIdioma = peliculasPorIdioma.get(idioma);
            if (peliculasIdioma != null && !peliculasIdioma.estaVacia()) {
                // Ordenar por total de evaluaciones (descendente)
                ordenarListaPeliculas(peliculasIdioma);

                // Mostrar top 5
                int contador = 0;
                ListaEnlazada.Nodo<PeliculaConteo> actual = peliculasIdioma.getCabeza();
                while (actual != null && contador < 5) {
                    PeliculaConteo pc = actual.getDato();
                    String titulo = pc.pelicula.getTitulo();

                    // Ajustar el título si es muy largo
                    if (titulo.length() > 40) {
                        titulo = titulo.substring(0, 37) + "...";
                    }

                    // Formatear la salida
                    System.out.printf("%-6d %-40s %,14d %7s%n",
                            pc.pelicula.getId(),
                            titulo,
                            pc.totalEvaluaciones,
                            pc.pelicula.getIdioma_original());

                    contador++;
                    actual = actual.getSiguiente();
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
    }



    // Clase auxiliar para almacenar película + conteo de evaluaciones
    private static class PeliculaConteo {
        Pelicula pelicula;
        int totalEvaluaciones;

        public PeliculaConteo(Pelicula pelicula, int totalEvaluaciones) {
            this.pelicula = pelicula;
            this.totalEvaluaciones = totalEvaluaciones;
        }
    }


    private void ordenarListaPeliculas(ListaEnlazada<PeliculaConteo> lista) {
        if (lista.estaVacia() || lista.tamanio() == 1) {
            return;
        }

        boolean intercambiado;
        do {
            intercambiado = false;
            ListaEnlazada.Nodo<PeliculaConteo> anterior = null;
            ListaEnlazada.Nodo<PeliculaConteo> actual = lista.getCabeza();
            ListaEnlazada.Nodo<PeliculaConteo> siguiente = actual != null ? actual.getSiguiente() : null;

            while (siguiente != null) {
                PeliculaConteo pcActual = actual.getDato();
                PeliculaConteo pcSiguiente = siguiente.getDato();

                if (pcActual.totalEvaluaciones < pcSiguiente.totalEvaluaciones) {
                    // Intercambiar nodos
                    actual.setSiguiente(siguiente.getSiguiente());
                    siguiente.setSiguiente(actual);

                    if (anterior == null) {
                        lista.setCabeza(siguiente);
                    } else {
                        anterior.setSiguiente(siguiente);
                    }

                    // Actualizar referencia para continuar
                    ListaEnlazada.Nodo<PeliculaConteo> temp = actual;
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

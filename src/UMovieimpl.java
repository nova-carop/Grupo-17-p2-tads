import Tads.MyHeapImpl;
import entidades.*;
import Tads.ListaEnlazada;
import Tads.TablaHash;
import interfaces.UMovieMgt;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class UMovieimpl implements UMovieMgt {
    private CargaDeDatos cargaDeDatos;

    public UMovieimpl(CargaDeDatos cargaDeDatos) {
        this.cargaDeDatos = cargaDeDatos;
    }

    //para pruebas unitarias
    public UMovieimpl() {
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
            ListaEnlazada<Integer> claves = reviewsPorPelicula.claves();
            for (int i = 0; i < claves.tamanio(); i++) {
                Integer idPelicula = claves.obtener(i);
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

    // Metodo auxiliar para contar reviews de una película
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

    // Metodo para ordenar películas por cantidad de reviews (descendente)
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
        // Traer las tablas películas y reviews por película
        TablaHash<Integer, Pelicula> tablaPeliculas = cargaDeDatos.getTablaPeliculas();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = cargaDeDatos.getReviewsPorPelicula();

        // Creacion lista para almacenar la información
        ListaEnlazada<Pelicula> peliculasConMedia = new ListaEnlazada<>();

        // Obtener ID de películas
        ListaEnlazada<Integer> idsPeliculas = reviewsPorPelicula.claves();

        // Iterar sobre cada ID de película
        for (int i = 0; i < idsPeliculas.tamanio(); i++) {
            Integer idPelicula = idsPeliculas.obtener(i);
            Pelicula pelicula = tablaPeliculas.get(idPelicula);

            if (pelicula != null) {
                ListaEnlazada<Review> reviews = reviewsPorPelicula.get(idPelicula);

                if (reviews == null || reviews.estaVacia()) {
                    continue;
                }

                float sumaCalificaciones = 0;
                int cantidadReseñas = 0;

                // Calcular suma y cantidad de calificaciones
                ListaEnlazada.Nodo<Review> nodo = reviews.getCabeza();
                while (nodo != null) {
                    Review review = nodo.getDato();
                    sumaCalificaciones += review.getCalificacion();
                    cantidadReseñas++;
                    nodo = nodo.getSiguiente();
                }

                // Solo considerar películas con más de 100 reseñas
                if (cantidadReseñas > 100) {
                    float calificacionMedia = sumaCalificaciones / cantidadReseñas;
                    pelicula.setCalificacion_media(calificacionMedia); // Actualizar calificación media
                    peliculasConMedia.insertar(pelicula);
                }
            }
        }

        // Ordenar por calificación media descendente
        ordenarListaPorMediaDescendente(peliculasConMedia);

        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("ID     Título                                      Evaluaciones  Calificación Media");
        System.out.println("-----------------------------------------------------------------------------------");

        int contador = 0;
        ListaEnlazada.Nodo<Pelicula> nodoActual = peliculasConMedia.getCabeza();
        while (nodoActual != null && contador < 10) {
            Pelicula pelicula = nodoActual.getDato();
            String titulo = pelicula.getTitulo();
            if (titulo.length() > 40) {
                titulo = titulo.substring(0, 37) + "...";
            }
            System.out.printf("%-6d %-40s %,14d %15.2f%n",
                    pelicula.getId(),
                    titulo,
                    contarReviews(reviewsPorPelicula.get(pelicula.getId())),
                    pelicula.getCalificacion_media());
            nodoActual = nodoActual.getSiguiente();
            contador++;
        }
    }

    private void ordenarListaPorMediaDescendente(ListaEnlazada<Pelicula> lista) {
        if (lista == null || lista.estaVacia()) return;

        boolean intercambiado;
        do {
            intercambiado = false;
            ListaEnlazada.Nodo<Pelicula> anterior = null;
            ListaEnlazada.Nodo<Pelicula> actual = lista.getCabeza();
            ListaEnlazada.Nodo<Pelicula> siguiente = actual != null ? actual.getSiguiente() : null;

            while (siguiente != null) {
                float califActual = actual.getDato().getCalificacion_media();
                float califSiguiente = siguiente.getDato().getCalificacion_media();

                if (califActual < califSiguiente) {
                    // Intercambiar nodos
                    actual.setSiguiente(siguiente.getSiguiente());
                    siguiente.setSiguiente(actual);

                    if (anterior == null) {
                        lista.setCabeza(siguiente);
                    } else {
                        anterior.setSiguiente(siguiente);
                    }

                    // Actualizar referencias
                    ListaEnlazada.Nodo<Pelicula> temp = actual;
                    actual = siguiente;
                    siguiente = temp;
                    intercambiado = true;
                }

                // Avanzar al siguiente nodo
                anterior = actual;
                actual = siguiente;
                if (actual != null) {
                    siguiente = actual.getSiguiente();
                }
            }
        } while (intercambiado);
    }

    @Override
    public void Top_5_de_las_colecciones_que_más_ingresos_generaron() {
        long inicio = System.currentTimeMillis();

        TablaHash<Integer, Pelicula> tablaPeliculas = cargaDeDatos.getTablaPeliculas();
        TablaHash<Integer, Saga> tablaSagas = cargaDeDatos.getTablaSagas();

        MyHeapImpl<Saga> top5 = new MyHeapImpl<>();

        //  Procesar sagas reales
        ListaEnlazada<Saga> sagas = tablaSagas.values();
        for (int i = 0; i < sagas.tamanio(); i++) {
            Saga saga = sagas.obtener(i);
            if (top5.tamaño() < 5) {
                top5.insertar(saga);
            } else if (saga.compareTo(top5.verTope()) > 0) {
                top5.eliminar();
                top5.insertar(saga);
            }
        }

        //  Procesar películas individuales como sagas virtuales
        ListaEnlazada<Pelicula> peliculas = tablaPeliculas.values();
        for (int i = 0; i < peliculas.tamanio(); i++) {
            Pelicula peli = peliculas.obtener(i);

            if (peli.getId_saga() == -1) {
                int ingreso = (int) peli.getIngreso();
                Saga virtual = new Saga(peli.getId(), peli.getTitulo(), 1, ingreso);

                if (top5.tamaño() < 5) {
                    top5.insertar(virtual);
                } else if (virtual.compareTo(top5.verTope()) > 0) {
                    top5.eliminar();
                    top5.insertar(virtual);
                }
            }
        }

        //  Extraer Top 5 en orden descendente
        ListaEnlazada<Saga> topOrdenado = new ListaEnlazada<>();
        while (!top5.estaVacio()) {
            topOrdenado.insertar(top5.eliminar()); // inserta al final
        }


        System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");

        // Imprimir cada colección
        for (int i = 0; i < topOrdenado.tamanio(); i++) {
            Saga coleccion = topOrdenado.obtener(i);
            System.out.print(coleccion.getId() + "," + coleccion.getTitulo() + "," + coleccion.getCantidad_peliculas() + ",[");

            // Buscar las películas que pertenecen a esta colección
            boolean primero = true;
            for (int j = 0; j < peliculas.tamanio(); j++) {
                Pelicula p = peliculas.obtener(j);

                // Película individual tratada como saga
                if (p.getId_saga() == -1 && p.getId() == coleccion.getId()) {
                    System.out.print(p.getId());
                    break;
                }

                // Película que pertenece a saga real
                if (p.getId_saga() == coleccion.getId()) {
                    if (!primero) System.out.print(",");
                    System.out.print(p.getId());
                    primero = false;
                }
            }

            System.out.println("]," + coleccion.getIngreso_generado());
        }

        long fin = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución de la consulta: " + (fin - inicio) + " ms");
    }




    @Override
    public void Top_10_de_los_directores_que_mejor_calificación_tienen() {
        // Traer las tablas Directores y ReviewsPorPelicula
        TablaHash<String, Director> tablaDirectores = cargaDeDatos.getDirectores();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = cargaDeDatos.getReviewsPorPelicula();

        // Comprobamos que ambas tablas estén cargadas
        if (tablaDirectores == null || tablaDirectores.isEmpty()) {
            System.err.println("Error: La tabla de directores está vacía o no fue cargada.");
            return;
        }
        if (reviewsPorPelicula == null || reviewsPorPelicula.isEmpty()) {
            System.err.println("Error: La tabla de reviews por película está vacía o no fue cargada.");
            return;
        }

        // Creamos una lista para guardar directores válidos
        ListaEnlazada<String> claves = tablaDirectores.claves();
        ListaEnlazada<Director> directoresValidos = new ListaEnlazada<>();

        // Recorremos todos los directores
        for (int i = 0; i < claves.tamanio(); i++) {
            String nombreDirector = claves.obtener(i);
            Director director = tablaDirectores.get(nombreDirector);

            // Validamos que tenga más de una película
            if (director.getPeliculasDirigidas() == null || director.getPeliculasDirigidas().tamanio() <= 1) {
                continue;
            }

            // Contador de reviews y lista de calificaciones para ese director
            int totalEvaluaciones = 0;
            ListaEnlazada<Double> calificacionesTotales = new ListaEnlazada<>();

            // Recorremos las películas del director
            for (int j = 0; j < director.getPeliculasDirigidas().tamanio(); j++) {
                Pelicula pelicula = director.getPeliculasDirigidas().obtener(j);

                // Obtenemos las reviews de esa película
                ListaEnlazada<Review> reviews = reviewsPorPelicula.get(pelicula.getId());

                if (reviews == null || reviews.estaVacia()) {
                    continue;
                }

                // Sumamos las calificaciones de las reviews
                for (int k = 0; k < reviews.tamanio(); k++) {
                    Review review = reviews.obtener(k);
                    calificacionesTotales.insertar((double) review.getCalificacion());
                    totalEvaluaciones++;
                }
            }

            // Si tiene más de 100 evaluaciones, es consideramos válido y calculamos su mediana
            if (totalEvaluaciones > 100) {
                double mediana = calcularMediana(calificacionesTotales);
                director.setMediana_calificacion((float) mediana);
                directoresValidos.insertar(director);
            }
        }

        // Ordenamos los directores por mediana descendente
        ordenarDirectoresPorMediana(directoresValidos);


        System.out.println("\nTop 10 de los directores que mejor calificación tienen:");
        System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");

        int contador = 0;
        for (int i = 0; i < directoresValidos.tamanio() && contador < 10; i++) {
            Director director = directoresValidos.obtener(i);
            System.out.printf("%s,%d,%.2f%n",
                    director.getNombre(),
                    director.getPeliculasDirigidas().tamanio(),
                    director.getMediana_calificacion());
            contador++;
        }
    }

    private void ordenarDirectoresPorMediana(ListaEnlazada<Director> directores) {
        for (int i = 0; i < directores.tamanio() - 1; i++) {
            for (int j = i + 1; j < directores.tamanio(); j++) {
                float m1 = directores.obtener(i).getMediana_calificacion();
                float m2 = directores.obtener(j).getMediana_calificacion();
                if (m1 < m2) {
                    directores.intercambiar(i, j);
                }
            }
        }
    }

    private double calcularMediana(ListaEnlazada<Double> valores) {
        if (valores.estaVacia()) return 0;

        int n = valores.tamanio();
        double[] arreglo = new double[n];

        for (int i = 0; i < n; i++) {
            arreglo[i] = valores.obtener(i);
        }

        Arrays.sort(arreglo);

        if (n % 2 == 1) {
            return arreglo[n / 2];
        } else {
            return (arreglo[(n / 2) - 1] + arreglo[n / 2]) / 2;
        }
    }


    @Override
    public void Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año() {
        long startTime = System.currentTimeMillis();

        // Array con nombres de los meses en español
        String[] nombresMeses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        TablaHash<Integer, Pelicula> tablaPeliculas = cargaDeDatos.getTablaPeliculas();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = cargaDeDatos.getReviewsPorPelicula();

        System.out.println("\nActor con más calificaciones por mes:");
        System.out.println("Mes           Nombre del actor          Películas  Calificaciones");
        System.out.println("---------------------------------------------------------------");

        for (int mes = 0; mes < 12; mes++) {
            TablaHash<String, int[]> contadoresActores = new TablaHash<>();

            ListaEnlazada<Integer> idsPeliculas = reviewsPorPelicula.claves();

            for (int i = 0; i < idsPeliculas.tamanio(); i++) {
                Integer idPelicula = idsPeliculas.obtener(i);
                Pelicula pelicula = tablaPeliculas.get(idPelicula);

                if (pelicula != null && pelicula.getActores() != null && pelicula.getFecha_publicacion() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(pelicula.getFecha_publicacion());
                    int mesPelicula = cal.get(Calendar.MONTH);  // 0-11

                    if (mesPelicula == mes) {
                        int cantidadReviews = contarReviews(reviewsPorPelicula.get(idPelicula));

                        ListaEnlazada<Actor> actores = pelicula.getActores();
                        ListaEnlazada.Nodo<Actor> nodoActor = actores.getCabeza();

                        while (nodoActor != null) {
                            Actor actor = nodoActor.getDato();
                            String nombreActor = actor.getNombre();

                            int[] contadores = contadoresActores.get(nombreActor);
                            if (contadores == null) {
                                contadores = new int[2];
                                contadoresActores.put(nombreActor, contadores);
                            }

                            contadores[0]++;
                            contadores[1] += cantidadReviews;

                            nodoActor = nodoActor.getSiguiente();
                        }
                    }
                }
            }

            String actorTop = "N/A";
            int maxCalificaciones = 0;
            int peliculasTop = 0;

            ListaEnlazada<String> nombresActores = contadoresActores.claves();
            for (int i = 0; i < nombresActores.tamanio(); i++) {
                String nombreActor = nombresActores.obtener(i);
                int[] contadores = contadoresActores.get(nombreActor);

                if (contadores[1] > maxCalificaciones) {
                    actorTop = nombreActor;
                    maxCalificaciones = contadores[1];
                    peliculasTop = contadores[0];
                }
            }

            // Formatear la salida con nombres de meses
            System.out.printf("%-12s %-25s %6d %12d%n",
                    nombresMeses[mes],
                    actorTop,
                    peliculasTop,
                    maxCalificaciones);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
    }




    @Override
    public void Usuarios_con_más_calificaciones_por_género() {
        return;

    }
}
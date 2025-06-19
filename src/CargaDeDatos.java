import entidades.*;
import Tads.TablaHash;
import Tads.ListaEnlazada;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class CargaDeDatos {
    private TablaHash<Integer, Pelicula> tablaPeliculas;
    private TablaHash<Integer, Saga> tablaSagas;
    private TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula;
    private TablaHash<Integer, ListaEnlazada<Actor>> actoresPorPelicula;
    private TablaHash<String, Director> directores;

    public CargaDeDatos() {
        this.tablaPeliculas = new TablaHash<>();
        this.tablaSagas = new TablaHash<>();
        this.reviewsPorPelicula = new TablaHash<>();
        this.actoresPorPelicula = new TablaHash<>();
        this.directores = new TablaHash<>();
    }

    // -------------------- Carga de Películas --------------------
    public void cargarPeliculasDesdeCSV(String filePath) {
        int peliculasCargadas = 0;
        int lineasInvalidas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            boolean primeraLinea = true;
            StringBuilder lineaCompleta = new StringBuilder();

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                lineaCompleta.append(linea);

                if (contarComillas(lineaCompleta.toString()) % 2 == 0) {
                    if (procesarLineaPelicula(lineaCompleta.toString())) {
                        peliculasCargadas++;
                    } else {
                        lineasInvalidas++;
                    }
                    lineaCompleta = new StringBuilder();
                } else {
                    lineaCompleta.append("\n");
                }
            }

            if (lineaCompleta.length() > 0) {
                if (procesarLineaPelicula(lineaCompleta.toString())) {
                    peliculasCargadas++;
                } else {
                    lineasInvalidas++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de peliculas: " + e.getMessage());
        }

        System.out.println("Películas cargadas: " + peliculasCargadas);
    }

    private boolean procesarLineaPelicula(String linea) {
        String[] campos = parsearLineaCSV(linea);

        if (campos == null || campos.length < 13) {
            return false;
        }

        Integer id = ValidadorDatos.validarEntero(campos[5]);
        if (id == null) {
            return false;
        }

        String titulo = ValidadorDatos.estaVacio(campos[8]) ? "Desconocido" : campos[8].replace("\"", "").trim();
        String idioma = ValidadorDatos.estaVacio(campos[7]) ? "en" : campos[7].replace("\"", "").trim();
        Date fecha = ValidadorDatos.validarFecha(campos[12].replace("\"", ""));

        Pelicula pelicula = new Pelicula(id, titulo, idioma, 0.0f, fecha);
        tablaPeliculas.put(id, pelicula);

        if (!ValidadorDatos.estaVacio(campos[1]) && !campos[1].equals("null")) {
            procesarSaga(campos[1], id);
        }

        return true;
    }

    private void procesarSaga(String datosSaga, int idPelicula) {
        String[] partes = datosSaga.split("'id':");
        if (partes.length < 2) return;

        Integer idSaga = ValidadorDatos.validarEntero(partes[1].split(",")[0].trim());
        if (idSaga == null) return;

        Saga saga = tablaSagas.get(idSaga);
        if (saga == null) {
            String nombreSaga = "Desconocido";
            if (datosSaga.contains("'name':")) {
                nombreSaga = datosSaga.split("'name':")[1].split(",")[0].replace("'", "").trim();
            }

            saga = new Saga();
            saga.setId(idSaga);
            saga.setTitulo(nombreSaga);
            saga.setCantidad_peliculas(1);
            tablaSagas.put(idSaga, saga);
        } else {
            saga.setCantidad_peliculas(saga.getCantidad_peliculas() + 1);
        }

        Pelicula pelicula = tablaPeliculas.get(idPelicula);
        if (pelicula != null) {
            pelicula.setId_saga(idSaga);
        }
    }

    // -------------------- Carga de Ratings --------------------
    public void cargarRatingsDesdeCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            boolean primeraLinea = true;
            int reviewId = 1;
            int ratingsCargados = 0;
            int ratingsInvalidos = 0;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (campos.length != 4) {
                    ratingsInvalidos++;
                    continue;
                }

                try {
                    Integer userId = ValidadorDatos.validarEntero(campos[0].trim());
                    Integer movieId = ValidadorDatos.validarEntero(campos[1].trim());
                    Float rating = ValidadorDatos.validarFloat(campos[2].trim());

                    if (userId == null || userId <= 0 ||
                            movieId == null || movieId <= 0 ||
                            rating == null || rating < 0.5f || rating > 5.0f ||
                            ValidadorDatos.validarEntero(campos[3].trim()) == null) {
                        ratingsInvalidos++;
                        continue;
                    }

                    Review review = new Review(reviewId++, userId, movieId, rating);
                    agregarReviewAPelicula(movieId, review);
                    ratingsCargados++;

                } catch (Exception e) {
                    ratingsInvalidos++;
                }
            }


            System.out.println(" Ratings cargados correctamente: " + ratingsCargados);


        } catch (IOException e) {
            System.err.println("Error al leer el archivo de ratings: " + e.getMessage());

        }
    }

    private void agregarReviewAPelicula(int movieId, Review review) {
        ListaEnlazada<Review> reviews = reviewsPorPelicula.get(movieId);
        if (reviews == null) {
            reviews = new ListaEnlazada<>();
            reviewsPorPelicula.put(movieId, reviews);
        }
        reviews.insertar(review);
        actualizarPromedioPelicula(movieId, reviews);
    }

    private void actualizarPromedioPelicula(int movieId, ListaEnlazada<Review> reviews) {
        Pelicula pelicula = tablaPeliculas.get(movieId);
        if (pelicula != null) {
            final float[] suma = {0};
            final int[] contador = {0};

            reviews.recorrer(r -> {
                suma[0] += r.getCalificacion();
                contador[0]++;
            });

            pelicula.setCalificacion_media(contador[0] > 0 ? suma[0] / contador[0] : 0);
        }
    }

    // -------------------- Métodos Auxiliares --------------------
    private String[] parsearLineaCSV(String linea) {
        return linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private int contarComillas(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '"') {
                count++;
            }
        }
        return count;
    }

    // -------------------- Getters --------------------
    public TablaHash<Integer, Pelicula> getTablaPeliculas() {
        return tablaPeliculas;
    }

    public TablaHash<Integer, Saga> getTablaSagas() {
        return tablaSagas;
    }

    public TablaHash<Integer, ListaEnlazada<Review>> getReviewsPorPelicula() {
        return reviewsPorPelicula;
    }



    // -------------------- Carga de Créditos --------------------
    public void cargarCreditosDesdeCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            boolean primeraLinea = true;
            int creditosCargados = 0;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] campos = parsearLineaCSV(linea);
                if (campos.length < 3) continue;

                Integer movieId = ValidadorDatos.validarEntero(campos[2]);
                if (movieId == null) continue;

                // Procesar cast (actores)
                if (!ValidadorDatos.estaVacio(campos[0])) {
                    procesarActores(campos[0], movieId);
                }

                // Procesar crew (directores)
                if (!ValidadorDatos.estaVacio(campos[1])) {
                    procesarDirectores(campos[1], movieId);
                }

                creditosCargados++;
            }

           ;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de créditos: " + e.getMessage());
        }
    }

    private void procesarActores(String jsonCast, int movieId) {
        // Limpiar y dividir el array de actores
        String actoresStr = jsonCast.replace("[{", "").replace("}]", "");
        String[] actores = actoresStr.split("\\},\\s*\\{");

        ListaEnlazada<Actor> actoresPelicula = actoresPorPelicula.get(movieId);
        if (actoresPelicula == null) {
            actoresPelicula = new ListaEnlazada<>();
            actoresPorPelicula.put(movieId, actoresPelicula);
        }

        for (String actorStr : actores) {
            String[] camposActor = actorStr.split(",\\s*");
            String nombreActor = null;

            // Buscar el campo 'name'
            for (String campo : camposActor) {
                if (campo.startsWith("'name':")) {
                    nombreActor = campo.split(":")[1].replace("'", "").trim();
                    break;
                }
            }

            if (nombreActor != null && !nombreActor.isEmpty()) {
                Actor actor = new Actor(nombreActor);
                actoresPelicula.insertar(actor);
            }
        }
    }

    private void procesarDirectores(String jsonCrew, int movieId) {
        // Limpiar y dividir el array de crew
        String crewData = jsonCrew.replace("[{", "").replace("}]", "");
        String[] crewMembers = crewData.split("\\},\\s*\\{");

        for (String crewMember : crewMembers) {
            String[] camposCrew = crewMember.split(",\\s*");
            String nombreDirector = null;
            String trabajo = null;

            // Buscar nombre y trabajo
            for (String campo : camposCrew) {
                if (campo.startsWith("'name':")) {
                    nombreDirector = campo.split(":")[1].replace("'", "").trim();
                } else if (campo.startsWith("'job':")) {
                    trabajo = campo.split(":")[1].replace("'", "").trim();
                }
            }

            // Solo procesar si es director
            if (nombreDirector != null && "Director".equalsIgnoreCase(trabajo)) {
                Director director = directores.get(nombreDirector);
                Pelicula pelicula = tablaPeliculas.get(movieId);

                if (director == null) {
                    ListaEnlazada<Pelicula> peliculasDirigidas = new ListaEnlazada<>();
                    if (pelicula != null) {
                        peliculasDirigidas.insertar(pelicula);
                    }
                    director = new Director(nombreDirector, 1, 0.0f, peliculasDirigidas);
                    directores.put(nombreDirector, director);
                } else {
                    director.setCantidad_pelis(director.getCantidad_pelis() + 1);
                    if (pelicula != null) {
                        director.getPeliculasDirigidas().insertar(pelicula);
                    }
                }

                // Actualizar mediana de calificaciones (versión con ListaEnlazada)
                if (pelicula != null) {
                    final float[] suma = {0};
                    final int[] contador = {0};

                    director.getPeliculasDirigidas().recorrer(p -> {
                        suma[0] += p.getCalificacion_media();
                        contador[0]++;
                    });

                    if (contador[0] > 0) {
                        director.setMediana_calificacion(suma[0] / contador[0]);
                    }
                }
            }
        }
    }


    public TablaHash<Integer, ListaEnlazada<Actor>> getActoresPorPelicula() {
        return actoresPorPelicula;
    }

    public TablaHash<String, Director> getDirectores() {
        return directores;
    }
}
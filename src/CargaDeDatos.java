import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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
    private TablaHash<String, Actor> actores;
    private TablaHash<String, Director> directores;

    public CargaDeDatos() {
        this.tablaPeliculas = new TablaHash<>();
        this.tablaSagas = new TablaHash<>();
        this.reviewsPorPelicula = new TablaHash<>();
        this.actores = new TablaHash<>();
        this.directores = new TablaHash<>();
    }


    public int cargarPeliculas(String filePath) {
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
        return peliculasCargadas;
    }

    private boolean procesarLineaPelicula(String linea) {
        String[] campos = parsearLineaCSV(linea);

        if (campos == null || campos.length < 14) {
            return false;
        }

        Integer id = ValidadorDatos.validarEntero(campos[5]);
        if (id == null) {
            return false;
        }

        String titulo = ValidadorDatos.estaVacio(campos[8]) ? "Desconocido" : campos[8].replace("\"", "").trim();
        String idioma = ValidadorDatos.estaVacio(campos[7]) ? "en" : campos[7].replace("\"", "").trim();
        Date fecha = ValidadorDatos.validarFecha(campos[12].replace("\"", ""));
        Integer revenue = ValidadorDatos.validarEntero(campos[13].replace("\"", "").trim());
        if (revenue == null) revenue = 0;

        // Crear película con ingresos
        Pelicula pelicula = new Pelicula(id, titulo, idioma, 0.0f, fecha);
        pelicula.setIngreso(revenue);  // ⬅️ ahora se guarda el ingreso
        tablaPeliculas.put(id, pelicula);

        // Si tiene saga, la procesamos y le sumamos el ingreso a la saga
        if (!ValidadorDatos.estaVacio(campos[1]) && !campos[1].equals("null")) {
            procesarSaga(campos[1], id, revenue);
        }

        return true;
    }


    private void procesarSaga(String datosSaga, int idPelicula, int revenuePelicula) {
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
            saga.setIngreso_generado(revenuePelicula);
            tablaSagas.put(idSaga, saga);
        } else {
            saga.setCantidad_peliculas(saga.getCantidad_peliculas() + 1);
            saga.setIngreso_generado(saga.getIngreso_generado() + revenuePelicula);
        }

        Pelicula pelicula = tablaPeliculas.get(idPelicula);
        if (pelicula != null) {
            pelicula.setId_saga(idSaga);
        }
    }


    public int cargarRatings(String filePath) {
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

            return ratingsCargados;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de ratings: " + e.getMessage());

        }

        return 0;
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


    public int cargarCreditos(String rutaCsv) {
        int procesadas = 0, errores = 0;
        final int MAX_ERRORES_MOSTRAR = 10;
        int erroresMostrados = 0;

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(rutaCsv))
                .withSkipLines(1)
                .build()) {

            String[] linea;
            int numeroLinea = 2;

            while ((linea = reader.readNext()) != null) {
                try {
                    // Validación básica de línea
                    if (linea.length < 3 || !ValidadorDatos.esCadenaValida(linea[2])) {
                        throw new IllegalArgumentException("Línea incompleta");
                    }

                    int idPelicula = Integer.parseInt(linea[2].trim());
                    Pelicula pelicula = tablaPeliculas.get(idPelicula);
                    if (pelicula == null) {
                        errores++;
                        continue;
                    }

                    // Procesar actores
                    if (linea.length > 0 && ValidadorDatos.esCadenaValida(linea[0])) {
                        procesarJsonActores(ValidadorDatos.limpiarYValidarJson(linea[0]), pelicula);
                    }

                    // Procesar directores
                    if (linea.length > 1 && ValidadorDatos.esCadenaValida(linea[1])) {
                        procesarJsonDirectores(ValidadorDatos.limpiarYValidarJson(linea[1]), pelicula);
                    }

                    procesadas++;


                } catch (Exception e) {
                    errores++;
                    if (erroresMostrados < MAX_ERRORES_MOSTRAR) {
                        System.out.println("Error línea " + numeroLinea + ": " + e.getMessage());
                        erroresMostrados++;
                    }
                }
                numeroLinea++;
            }

        } catch (Exception e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
        }

        return procesadas;
    }

    private void procesarJsonActores(String json, Pelicula pelicula) {
        try {
            JsonNode castArray = new ObjectMapper().readTree(json);

            if (pelicula.getActores() == null) {
                pelicula.setActores(new ListaEnlazada<>());
            }

            for (JsonNode actorNode : castArray) {
                if (actorNode.has("name")) {
                    String nombre = actorNode.get("name").asText();
                    if (ValidadorDatos.esCadenaValida(nombre)) {
                        Actor actor = actores.get(nombre);
                        if (actor == null) {
                            actor = new Actor(nombre, 0);
                            actores.put(nombre, actor);
                        }
                        pelicula.getActores().insertar(actor);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando actores: " + e.getMessage());
        }
    }

    private void procesarJsonDirectores(String json, Pelicula pelicula) {
        try {
            JsonNode crewArray = new ObjectMapper().readTree(json);

            for (JsonNode miembro : crewArray) {
                if (miembro.has("job") && "Director".equals(miembro.get("job").asText()) && miembro.has("name")) {
                    String nombre = miembro.get("name").asText();
                    if (ValidadorDatos.esCadenaValida(nombre)) {

                        Director director = directores.get(nombre);
                        if (director == null) {
                            director = new Director(nombre, 0, 0, new ListaEnlazada<>());
                            directores.put(nombre, director);
                        }

                        if (director.getPeliculasDirigidas() == null) {
                            director.setPeliculas(new ListaEnlazada<>());
                        }
                        director.getPeliculasDirigidas().insertar(pelicula);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando directores: " + e.getMessage());
        }
    }



    public TablaHash<Integer, Pelicula> getTablaPeliculas() {
        return tablaPeliculas;
    }

    public TablaHash<Integer, Saga> getTablaSagas() {
        return tablaSagas;
    }

    public TablaHash<Integer, ListaEnlazada<Review>> getReviewsPorPelicula() {
        return reviewsPorPelicula;
    }

    public TablaHash<String, Director> getDirectores() {
        return directores;
    }
}
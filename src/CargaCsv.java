import Tads.ColaPrioridad;
import com.fasterxml.jackson.databind.ObjectMapper;
import entidades.*;
import Tads.TablaHash;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class CargaCsv {

    public static TablaHash<Integer, Pelicula> cargarPeliculas() throws Exception {
        TablaHash<Integer, Pelicula> peliculas = new TablaHash<>();

        InputStream inputStream = CargaCsv.class.getClassLoader().getResourceAsStream("movies_metadata.csv");
        if (inputStream == null) {
            throw new RuntimeException("No se encontró el archivo movies_metadata.csv");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String linea;
        boolean primera = true;
        int errores = 0;

        while ((linea = reader.readLine()) != null) {
            if (primera) {
                primera = false;
                continue;
            }

            String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            try {
                if (campos.length <= 18) continue;

                String idStr = campos[5];
                String idioma = campos[7];
                String titulo = campos[18];
                String fechaStr = campos[12];

                if (ValidadorCsv.esCampoVacio(idStr) || ValidadorCsv.esCampoVacio(idioma) || ValidadorCsv.esCampoVacio(titulo)) {
                    continue;
                }

                int id = ValidadorCsv.parsearEntero(idStr);
                float calificacion_media = 0.0f;
                Date fecha_publicacion = null;

                if (!ValidadorCsv.esCampoVacio(fechaStr)) {
                    try {
                        fecha_publicacion = ValidadorCsv.parsearFecha(fechaStr);
                    } catch (Exception exFecha) {
                        // ignorar fecha inválida
                    }
                }

                if (!peliculas.containsKey(id)) {
                    Pelicula pelicula = new Pelicula(id, titulo.trim(), idioma.trim(), calificacion_media, fecha_publicacion);
                    peliculas.put(id, pelicula);
                }

            } catch (Exception e) {
                errores++;
            }
        }

        reader.close();
        return peliculas;
    }


    public static ColaPrioridad<Review> cargarRatings() throws Exception {

        //CAMBIARLO CON UN HASH
        ColaPrioridad<Review> ratings = new ColaPrioridad<>();

        InputStream inputStream = CargaCsv.class.getClassLoader().getResourceAsStream("ratings_1mm.csv");
        if (inputStream == null) {
            throw new RuntimeException("No se encontró el archivo ratings_1mm.csv");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String linea;
        boolean primera = true;
        int errores = 0;
        int idAutoIncrement = 1; //xq el csv de ratings no tiene una columna de id

        while ((linea = reader.readLine()) != null) {
            if (primera) {
                primera = false;
                continue;
            }

            String[] campos = linea.split(",");

            try {
                if (campos.length < 3) {
                    errores++;
                    continue;
                }

                String idUsuarioStr = campos[0];
                String idPeliculaStr = campos[1];
                String calificacionStr = campos[2];

                if (ValidadorCsv.esCampoVacio(idUsuarioStr) || ValidadorCsv.esCampoVacio(idPeliculaStr) || ValidadorCsv.esCampoVacio(calificacionStr)) {
                    errores++;
                    continue;
                }

                int id_usuario = ValidadorCsv.parsearEntero(idUsuarioStr);
                int id_pelicula = ValidadorCsv.parsearEntero(idPeliculaStr);
                float calificacion = Float.parseFloat(calificacionStr.trim());

                if (calificacion < 0.0 || calificacion > 5.0) {
                    errores++;
                    continue;
                }

                Review review = new Review(idAutoIncrement++, id_usuario, id_pelicula, calificacion);
                ratings.insertar(review);

            } catch (Exception e) {
                errores++;
            }
        }

        reader.close();
        return ratings;
    }



    public static void cargarCreditos(TablaHash<Integer, Pelicula> peliculas, TablaHash<String, Director> directores, TablaHash<String, Actor> actores) throws Exception {
        InputStream inputStream = CargaCsv.class.getClassLoader().getResourceAsStream("credits.csv");
        if (inputStream == null) {
            throw new RuntimeException("No se encontró el archivo credits.csv");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ObjectMapper mapper = new ObjectMapper();

        String linea;
        boolean primera = true;
        int errores = 0;
        int contador = 0;

        while ((linea = reader.readLine()) != null) {
            if (primera) {
                primera = false;
                continue;
            }

            contador++;
            if (contador % 2000 == 0) {
                System.out.println("Procesadas: " + contador);
            }

            try {
                //no usar un split con regex
                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (campos.length < 3) {
                    errores++;
                    continue;
                }

                int idPelicula = Integer.parseInt(campos[2].trim());
                Pelicula pelicula = peliculas.get(idPelicula);
                if (pelicula == null) {
                    errores++;
                    continue;
                }

                String jsonCast = campos[0].trim();
                String jsonCrew = campos[1].trim();

                // CAST
                MiembroCast[] miembrosCast = mapper.readValue(jsonCast, MiembroCast[].class);
                for (MiembroCast mc : miembrosCast) {
                    if (mc.name == null || mc.name.isEmpty()) continue;

                    Actor actorExistente = actores.get(mc.name);
                    if (actorExistente == null) {
                        actorExistente = new Actor(mc.name);
                        actores.put(mc.name, actorExistente);
                    }

                    pelicula.getActores().insertar(actorExistente);
                }

                // CREW (buscar solo director)
                MiembroCrew[] miembrosCrew = mapper.readValue(jsonCrew, MiembroCrew[].class);
                for (MiembroCrew miembro : miembrosCrew) {
                    if ("Director".equalsIgnoreCase(miembro.job)) {
                        if (miembro.name == null || miembro.name.isEmpty()) break;

                        Director director = directores.get(miembro.name);
                        if (director == null) {
                            // Si no existe  omitimos esta fila
                            errores++;
                            break;
                        }

                        director.getPeliculasDirigidas().insertar(pelicula);
                        break; // sólo un director por película
                    }
                }

            } catch (Exception e) {
                errores++;
            }
        }

        reader.close();

    }



    //3 TABLAS HASH PARA LA CARGA DE LOS CSV

}


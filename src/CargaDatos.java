
import entidades.Pelicula;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CargaDatos {

    public static List<Pelicula> cargarPeliculas() throws Exception {
        List<Pelicula> peliculas = new ArrayList<>();

        InputStream inputStream = CargaDatos.class.getClassLoader().getResourceAsStream("movies_metadata.csv");
        if (inputStream == null) {
            throw new RuntimeException("No se encontró el archivo movies_metadata.csv");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String linea;
        boolean primera = true;
        int errores = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        while ((linea = reader.readLine()) != null) {
            if (primera) {
                primera = false;
                continue;
            }

            String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            try {
                if (campos.length <= 18) continue;

                String idStr = campos[5].trim();
                String idioma = campos[7].trim();
                String titulo = campos[18].trim();
                String fechaStr = campos[12].trim();

                if (idStr.isEmpty() || idioma.isEmpty() || titulo.isEmpty()) continue;

                int id = Integer.parseInt(idStr);
                float calificacion_media = 0.0f;
                Date fecha_publicacion = null;

                if (!fechaStr.isEmpty()) {
                    try {
                        fecha_publicacion = sdf.parse(fechaStr);
                    } catch (Exception exFecha) {
                        // ignorar fecha inválida
                    }
                }

                Pelicula pelicula = new Pelicula(id, titulo, idioma, calificacion_media, fecha_publicacion);
                peliculas.add(pelicula);

            } catch (Exception e) {
                errores++;
            }
        }

        reader.close();

        System.out.println("Películas cargadas correctamente: " + peliculas.size());
        System.out.println("Líneas con error: " + errores);
        return peliculas;
    }
}

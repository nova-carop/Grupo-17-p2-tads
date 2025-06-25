import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import Tads.ListaEnlazada;
import Tads.TablaHash;
import entidades.Actor;
import entidades.Pelicula;
import entidades.Review;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Calendar;

public class UMovieimplTest {

    private static class CargaDeDatosTest extends CargaDeDatos {
        private TablaHash<Integer, Pelicula> tablaPeliculas;
        private TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula;

        public CargaDeDatosTest(TablaHash<Integer, Pelicula> peliculas,
                                TablaHash<Integer, ListaEnlazada<Review>> reviews) {
            this.tablaPeliculas = peliculas;
            this.reviewsPorPelicula = reviews;
        }

        @Override
        public TablaHash<Integer, Pelicula> getTablaPeliculas() {
            return tablaPeliculas;
        }

        @Override
        public TablaHash<Integer, ListaEnlazada<Review>> getReviewsPorPelicula() {
            return reviewsPorPelicula;
        }
    }

    private UMovieimpl uMovieImpl;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capturar la salida del System.out para verificar los prints
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    // ============ CAMINO FELIZ ============

    @Test
//Camino Feliz: Top 5 películas con reviews en múltiples idiomas
    void testTop5PeliculasCaminoFeliz() {
        // Arrange - Crear datos de prueba
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasCaminoFeliz();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsCaminoFeliz();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();

        // Verificar que se muestran los idiomas esperados
        assert output.contains("Idioma: en");
        assert output.contains("Idioma: fr");
        assert output.contains("Idioma: it");
        assert output.contains("Idioma: es");
        assert output.contains("Idioma: pt");

        // Verificar que se muestran las películas ordenadas por cantidad de reviews
        assert output.contains("Película Inglesa A"); // 5 reviews
        assert output.contains("Película Inglesa B"); // 3 reviews
        assert output.contains("Película Francesa A"); // 4 reviews
        assert output.contains("Tiempo de ejecución:");
    }

    // ============ CAMINOS TRISTES ============

    @Test
    //Camino Triste: Tabla de películas vacía
    void testTop5PeliculasTablaVacia() {

        TablaHash<Integer, Pelicula> tablaPeliculasVacia = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculasVacia, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();


        String output = outputStream.toString();
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");
        assert output.contains("Tiempo de ejecución:");

        // No debería mostrar películas para ningún idioma
        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma);
        }
    }

    @Test
//Camino Triste: Películas sin reviews - NO deben aparecer en el ranking
    void testTop5PeliculasSinReviews() {

        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSinReviews();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);


        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();

        // Verificar que se procesan los idiomas
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");
        assert output.contains("Tiempo de ejecución:");

        // Verificar que se muestran las secciones de idiomas
        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma);
        }

        // VALIDACIÓN CLAVE: Las películas sin reviews NO deben aparecer en el ranking
        assert !output.contains("Película Sin Reviews") :
                "Las películas sin reviews NO deberían aparecer en el top 5";

        // Verificar que no hay información de películas después de cada sección de idioma
        // (esto implica que no se listaron películas para ningún idioma)
        String[] secciones = output.split("Idioma: ");
        for (int i = 1; i < secciones.length; i++) {
            String seccion = secciones[i];
            // Cada sección debería solo contener el nombre del idioma, no títulos de películas
            assert !seccion.contains("1.") :
                    "No deberían aparecer películas numeradas en idioma sin reviews";
        }
    }

    @Test
//Camino Triste: Solo películas de idiomas no objetivo
    void testTop5PeliculasIdiomasNoObjetivo() {

        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasIdiomasNoObjetivo();
        TablaHash<Integer, ListaEnlazada<Review>> reviews = crearReviewsIdiomasNoObjetivo();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviews);
        uMovieImpl = new UMovieimpl(cargaDatos);


        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();


        String output = outputStream.toString();

        // Verificar que se procesan los idiomas objetivo
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");

        // Verificar que se muestran SOLO los idiomas objetivo
        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma) :
                    "Debe procesar el idioma objetivo: " + idioma;
        }

        // VALIDACIÓN CLAVE: Películas en idiomas NO objetivo NO deben aparecer
        assert !output.contains("Película Alemana") :
                "Películas en alemán (de) NO deberían aparecer en el ranking";
        assert !output.contains("Película Japonesa") :
                "Películas en japonés (ja) NO deberían aparecer en el ranking";

        // Verificar que NO aparecen los idiomas no objetivo en el output
        assert !output.contains("Idioma: de") :
                "El alemán no debería aparecer como idioma procesado";
        assert !output.contains("Idioma: ja") :
                "El japonés no debería aparecer como idioma procesado";

        // Verificar que las secciones de idiomas objetivo están vacías
        // (no contienen películas numeradas)
        String[] secciones = output.split("Idioma: ");
        for (int i = 1; i < secciones.length; i++) {
            String seccion = secciones[i];
            // Verificar que no hay películas listadas (no aparece numeración "1.", "2.", etc.)
            assert !seccion.contains("1.") :
                    "No deberían aparecer películas numeradas para idiomas objetivo cuando solo hay idiomas no objetivo";
        }
    }

    @Test
   //Camino Triste: Menos de 5 películas por idioma
    void testTop5PeliculasMenosDe5PorIdioma() {

        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasPocas();
        TablaHash<Integer, ListaEnlazada<Review>> reviews = crearReviewsPocas();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviews);
        uMovieImpl = new UMovieimpl(cargaDatos);


        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");
        assert output.contains("Película Inglesa Unica");
        assert output.contains("Película Francesa Unica");
    }


    @Test
//Camino Triste: CargaDeDatos retorna null
    void testTop5PeliculasCargaDatosNull() {

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(null, null);
        uMovieImpl = new UMovieimpl(cargaDatos);

        //Debería lanzar NullPointerException
        try {
            uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();
            assert false : "Debería haber lanzado NullPointerException";
        } catch (NullPointerException e) {
            // Comportamiento esperado
            assert true;
        }
    }

    // ============ MÉTODOS AUXILIARES PARA CREAR DATOS DE PRUEBA ============

    private TablaHash<Integer, Pelicula> crearTablaPeliculasCaminoFeliz() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        // Películas en inglés
        tabla.put(1, crearPelicula(1, "Película Inglesa A", "en"));
        tabla.put(2, crearPelicula(2, "Película Inglesa B", "en"));
        tabla.put(3, crearPelicula(3, "Película Inglesa C", "en"));

        // Películas en francés
        tabla.put(4, crearPelicula(4, "Película Francesa A", "fr"));
        tabla.put(5, crearPelicula(5, "Película Francesa B", "fr"));

        // Películas en italiano
        tabla.put(6, crearPelicula(6, "Película Italiana A", "it"));

        // Películas en español
        tabla.put(7, crearPelicula(7, "Película Española A", "es"));

        // Películas en portugués
        tabla.put(8, crearPelicula(8, "Película Portuguesa A", "pt"));

        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsCaminoFeliz() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();

        // Película 1 (inglés) - 5 reviews
        tabla.put(1, crearListaReviews(5));

        // Película 2 (inglés) - 3 reviews
        tabla.put(2, crearListaReviews(3));

        // Película 3 (inglés) - 2 reviews
        tabla.put(3, crearListaReviews(2));

        // Película 4 (francés) - 4 reviews
        tabla.put(4, crearListaReviews(4));

        // Película 5 (francés) - 1 review
        tabla.put(5, crearListaReviews(1));

        // Película 6 (italiano) - 3 reviews
        tabla.put(6, crearListaReviews(3));

        // Película 7 (español) - 2 reviews
        tabla.put(7, crearListaReviews(2));

        // Película 8 (portugués) - 1 review
        tabla.put(8, crearListaReviews(1));

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasSinReviews() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();
        tabla.put(1, crearPelicula(1, "Película Sin Reviews", "en"));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasIdiomasNoObjetivo() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();
        tabla.put(1, crearPelicula(1, "Película Alemana", "de"));
        tabla.put(2, crearPelicula(2, "Película Japonesa", "ja"));
        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsIdiomasNoObjetivo() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();
        tabla.put(1, crearListaReviews(3));
        tabla.put(2, crearListaReviews(2));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasPocas() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();
        tabla.put(1, crearPelicula(1, "Película Inglesa Unica", "en"));
        tabla.put(2, crearPelicula(2, "Película Francesa Unica", "fr"));
        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsPocas() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();
        tabla.put(1, crearListaReviews(2));
        tabla.put(2, crearListaReviews(1));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasTituloLargo() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();
        tabla.put(1, crearPelicula(1, "Esta es una película con un título extremadamente largo que debería ser truncado en la salida", "en"));
        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsTituloLargo() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();
        tabla.put(1, crearListaReviews(3));
        return tabla;
    }

    private Pelicula crearPelicula(int id, String titulo, String idioma) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setIdioma_original(idioma);
        return pelicula;
    }

    private ListaEnlazada<Review> crearListaReviews(int cantidad) {
        ListaEnlazada<Review> lista = new ListaEnlazada<>();
        for (int i = 0; i < cantidad; i++) {
            Review review = new Review();
            review.setId(i + 1);
            review.setCalificacion(5.0f);
            lista.insertar(review);
        }
        return lista;
    }

    // Restaurar System.out después de cada test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
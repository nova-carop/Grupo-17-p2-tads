import entidades.Pelicula;
import entidades.Review;
import Tads.ListaEnlazada;
import Tads.TablaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UMovieimplTest {

    private UMovieimpl umovie;
    private CargaDeDatosFalsa cargaDatosFalsa;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Configurar salida estándar para capturar System.out
        System.setOut(new PrintStream(outContent));

        // Crear instancia de datos falsos
        cargaDatosFalsa = new CargaDeDatosFalsa();
        umovie = new UMovieimpl(cargaDatosFalsa);
    }

    @BeforeEach
    void tearDown() {
        // Restaurar salida estándar
        System.setOut(originalOut);
    }

    // Implementación falsa de CargaDeDatos
    static class CargaDeDatosFalsa extends CargaDeDatos {
        private final TablaHash<Integer, Pelicula> tablaPeliculas = new TablaHash<>();
        private final TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = new TablaHash<>();

        public void agregarPelicula(Pelicula pelicula, ListaEnlazada<Review> reviews) {
            tablaPeliculas.put(pelicula.getId(), pelicula);
            reviewsPorPelicula.put(pelicula.getId(), reviews);
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

    // Método auxiliar para crear reviews
    private ListaEnlazada<Review> crearReviews(int cantidad) {
        ListaEnlazada<Review> reviews = new ListaEnlazada<>();
        for (int i = 0; i < cantidad; i++) {
            reviews.insertar(new Review());
        }
        return reviews;
    }

    // --- Pruebas ---

    @Test
    void testTop5PorIdioma_CaminoFeliz() {
        // Agregar películas de prueba
        Pelicula peliculaEn1 = new Pelicula(1, "Movie EN 1", "en", 0.0f, new Date());
        Pelicula peliculaEn2 = new Pelicula(2, "Movie EN 2", "en", 0.0f, new Date());
        Pelicula peliculaFr1 = new Pelicula(3, "Movie FR 1", "fr", 0.0f, new Date());
        Pelicula peliculaIt1 = new Pelicula(4, "Movie IT 1", "it", 0.0f, new Date());
        Pelicula peliculaEs1 = new Pelicula(5, "Movie ES 1", "es", 0.0f, new Date());
        Pelicula peliculaPt1 = new Pelicula(6, "Movie PT 1", "pt", 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(peliculaEn1, crearReviews(100));
        cargaDatosFalsa.agregarPelicula(peliculaEn2, crearReviews(200));
        cargaDatosFalsa.agregarPelicula(peliculaFr1, crearReviews(50));
        cargaDatosFalsa.agregarPelicula(peliculaIt1, crearReviews(30));
        cargaDatosFalsa.agregarPelicula(peliculaEs1, crearReviews(20));
        cargaDatosFalsa.agregarPelicula(peliculaPt1, crearReviews(10));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar resultados
        String output = outContent.toString();
        assertTrue(output.contains("Top 5 de películas con más evaluaciones por idioma"));
        assertTrue(output.contains("ID     Título"));
        assertTrue(output.contains("Evaluaciones"));
        assertTrue(output.contains("Idioma"));
        assertTrue(output.contains("=== en ==="));
        assertTrue(output.contains("=== fr ==="));
        assertTrue(output.contains("=== it ==="));
        assertTrue(output.contains("=== es ==="));
        assertTrue(output.contains("=== pt ==="));
        assertTrue(output.contains("2      Movie EN 2"));
        assertTrue(output.contains("1      Movie EN 1"));
        assertTrue(output.contains("3      Movie FR 1"));
        assertTrue(output.contains("4      Movie IT 1"));
        assertTrue(output.contains("5      Movie ES 1"));
        assertTrue(output.contains("6      Movie PT 1"));
    }

    @Test
    void testTop5PorIdioma_IdiomaSinPeliculas() {
        // Agregar solo películas en inglés
        Pelicula peliculaEn1 = new Pelicula(1, "Movie EN 1", "en", 0.0f, new Date());
        Pelicula peliculaEn2 = new Pelicula(2, "Movie EN 2", "en", 0.0f, new Date());
        cargaDatosFalsa.agregarPelicula(peliculaEn1, crearReviews(100));
        cargaDatosFalsa.agregarPelicula(peliculaEn2, crearReviews(200));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que solo se muestra inglés
        String output = outContent.toString();
        assertTrue(output.contains("=== en ==="));
        assertTrue(output.contains("2      Movie EN 2"));
        assertTrue(output.contains("1      Movie EN 1"));
        assertFalse(output.contains("Movie FR"));
        assertFalse(output.contains("Movie IT"));
        assertFalse(output.contains("Movie ES"));
        assertFalse(output.contains("Movie PT"));
    }

    @Test
    void testTop5PorIdioma_MenosDe5Peliculas() {
        // Solo 3 películas en inglés
        Pelicula peliculaEn1 = new Pelicula(1, "Movie EN 1", "en", 0.0f, new Date());
        Pelicula peliculaEn2 = new Pelicula(2, "Movie EN 2", "en", 0.0f, new Date());
        Pelicula peliculaEn3 = new Pelicula(3, "Movie EN 3", "en", 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(peliculaEn1, crearReviews(100));
        cargaDatosFalsa.agregarPelicula(peliculaEn2, crearReviews(200));
        cargaDatosFalsa.agregarPelicula(peliculaEn3, crearReviews(300));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que solo se muestran 3 películas para inglés
        String output = outContent.toString();
        assertTrue(output.contains("Movie EN 1"));
        assertTrue(output.contains("Movie EN 2"));
        assertTrue(output.contains("Movie EN 3"));
        assertFalse(output.contains("Movie EN 4"));

        // Contar cuántas veces aparece "en" en la salida (deberían ser 3)
        long count = output.lines()
                .filter(line -> line.contains("en"))
                .count();
        assertEquals(3, count);
    }

    @Test
    void testTop5PorIdioma_PeliculasSinReviews() {
        // Película con reviews
        Pelicula peliculaConReviews = new Pelicula(1, "Movie with Reviews", "en", 0.0f, new Date());

        // Película sin reviews
        Pelicula peliculaSinReviews = new Pelicula(2, "Movie without Reviews", "en", 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(peliculaConReviews, crearReviews(100));
        cargaDatosFalsa.agregarPelicula(peliculaSinReviews, new ListaEnlazada<>());

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que solo se muestra la película con reviews
        String output = outContent.toString();
        assertTrue(output.contains("Movie with Reviews"));
        assertFalse(output.contains("Movie without Reviews"));
    }

    @Test
    void testTop5PorIdioma_OrdenamientoCorrecto() {
        // Crear películas con diferentes cantidades de reviews
        Pelicula pelicula1 = new Pelicula(1, "Movie 100", "en", 0.0f, new Date());
        Pelicula pelicula2 = new Pelicula(2, "Movie 300", "en", 0.0f, new Date());
        Pelicula pelicula3 = new Pelicula(3, "Movie 200", "en", 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(pelicula1, crearReviews(100));
        cargaDatosFalsa.agregarPelicula(pelicula2, crearReviews(300));
        cargaDatosFalsa.agregarPelicula(pelicula3, crearReviews(200));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar resultados
        String output = outContent.toString();

        // Obtener líneas de la salida
        String[] lines = output.split("\n");

        // Buscar las líneas que contienen las películas
        int indexMovie300 = -1;
        int indexMovie200 = -1;
        int indexMovie100 = -1;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("Movie 300")) indexMovie300 = i;
            if (lines[i].contains("Movie 200")) indexMovie200 = i;
            if (lines[i].contains("Movie 100")) indexMovie100 = i;
        }

        // Verificar que las películas aparecen en el orden correcto (mayor a menor)
        assertTrue(indexMovie300 < indexMovie200, "Movie 300 debería aparecer antes que Movie 200");
        assertTrue(indexMovie200 < indexMovie100, "Movie 200 debería aparecer antes que Movie 100");
    }



    @Test
    void testTop5PorIdioma_DatosVacios() {
        // Ejecutar método con datos vacíos
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que no se muestra ningún resultado
        String output = outContent.toString();
        assertTrue(output.contains("Top 5 de películas con más evaluaciones por idioma"));
        assertFalse(output.contains("Movie"));
    }

    @Test
    void testTop5PorIdioma_PeliculaSinIdioma() {
        // Película sin idioma
        Pelicula peliculaSinIdioma = new Pelicula(1, "Movie without Language", null, 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(peliculaSinIdioma, crearReviews(100));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que la película sin idioma no se muestra
        String output = outContent.toString();
        assertFalse(output.contains("Movie without Language"));
    }

    @Test
    void testTop5PorIdioma_IdiomaNoObjetivo() {
        // Película en idioma no objetivo (alemán)
        Pelicula peliculaDe = new Pelicula(1, "Deutsche Film", "de", 0.0f, new Date());

        cargaDatosFalsa.agregarPelicula(peliculaDe, crearReviews(100));

        // Ejecutar método
        umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        // Verificar que la película en alemán no se muestra
        String output = outContent.toString();
        assertFalse(output.contains("Deutsche Film"));
    }
}
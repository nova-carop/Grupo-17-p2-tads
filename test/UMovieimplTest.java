import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tads.*;
import entidades.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Calendar;

public class UMovieimplTest {

    // Implementación simple de CargaDeDatos para testing
    private static class CargaDeDatosTest extends CargaDeDatos {
        private TablaHash<Integer, Pelicula> tablaPeliculas;
        private TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula;
        private TablaHash<Integer, Saga> tablaSagas;

        public CargaDeDatosTest(TablaHash<Integer, Pelicula> peliculas,
                                TablaHash<Integer, ListaEnlazada<Review>> reviews) {
            this.tablaPeliculas = peliculas;
            this.reviewsPorPelicula = reviews;
            this.tablaSagas = new TablaHash<>();
        }

        @Override
        public TablaHash<Integer, Pelicula> getTablaPeliculas() {
            return tablaPeliculas;
        }

        @Override
        public TablaHash<Integer, ListaEnlazada<Review>> getReviewsPorPelicula() {
            return reviewsPorPelicula;
        }

        public void setTablaSagas(TablaHash<Integer, Saga> tablaSagas) {
            this.tablaSagas = tablaSagas;
        }

        @Override
        public TablaHash<Integer, Saga> getTablaSagas() {
            return tablaSagas != null ? tablaSagas : new TablaHash<>();
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

    // ============ TESTS PARA TOP 5 PELÍCULAS POR IDIOMA ============

    @Test
        // Camino Feliz: múltiples idiomas con reviews
    void testTop5PeliculasCaminoFeliz() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasCaminoFeliz();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsCaminoFeliz();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();

        assert output.contains("Idioma: en");
        assert output.contains("Idioma: fr");
        assert output.contains("Idioma: it");
        assert output.contains("Idioma: es");
        assert output.contains("Idioma: pt");
        assert output.contains("Película Inglesa A"); // 5 reviews
        assert output.contains("Película Inglesa B"); // 3 reviews
        assert output.contains("Película Francesa A"); // 4 reviews
        assert output.contains("Tiempo de ejecución:");
    }

    @Test
        // Camino Triste: tabla de películas vacía
    void testTop5PeliculasTablaVacia() {
        TablaHash<Integer, Pelicula> tablaPeliculasVacia = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculasVacia, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");
        assert output.contains("Tiempo de ejecución:");

        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma);
        }
    }

    @Test
        // Camino Triste: películas sin reviews
    void testTop5PeliculasSinReviews() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSinReviews();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");
        assert output.contains("Tiempo de ejecución:");

        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma);
        }
    }

    @Test
        // Camino Triste: solo idiomas no objetivo
    void testTop5PeliculasIdiomasNoObjetivo() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasIdiomasNoObjetivo();
        TablaHash<Integer, ListaEnlazada<Review>> reviews = crearReviewsIdiomasNoObjetivo();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviews);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();
        assert output.contains("Top 5 de películas con más evaluaciones por idioma:");

        for (String idioma : new String[]{"en", "fr", "it", "es", "pt"}) {
            assert output.contains("Idioma: " + idioma);
        }
    }

    @Test
        // Camino Triste: menos de 5 películas por idioma
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
        // Camino Triste: títulos muy largos
    void testTop5PeliculasTituloLargo() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasTituloLargo();
        TablaHash<Integer, ListaEnlazada<Review>> reviews = crearReviewsTituloLargo();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviews);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

        String output = outputStream.toString();
        assert output.contains("..."); // Verificar que se trunca el título
        assert output.contains("Esta es una película con un título");
    }

    @Test
        // Camino Triste: CargaDeDatos retorna null
    void testTop5PeliculasCargaDatosNull() {
        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(null, null);
        uMovieImpl = new UMovieimpl(cargaDatos);

        try {
            uMovieImpl.Top_5_de_las_películas_que_más_calificaciones_por_idioma();
            assert false : "Debería haber lanzado NullPointerException";
        } catch (NullPointerException e) {
            assert true;
        }
    }

    // ============ TESTS PARA TOP 10 PELÍCULAS CON MEJOR CALIFIACION MEDIA ============
    @Test
    //Camino feliz
    void testTop10PeliculasConMasDe100ReviewsF() {
        // Crear una tabla hash de películas con reseñas válidas
        TablaHash<Integer, Pelicula> tablaPeliculas = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = new TablaHash<>();

        // Película 1: Tiene 150 reseñas con calificación promedio 4.7
        Pelicula pelicula1 = new Pelicula(1, "The Godfather", "en", 0.0f, new Date());
        ListaEnlazada<Review> reviews1 = new ListaEnlazada<>();
        for (int i = 0; i < 150; i++) {
            reviews1.insertar(new Review(i + 1, 1, 1, 4.7f));
        }
        tablaPeliculas.put(1, pelicula1);
        reviewsPorPelicula.put(1, reviews1);

        // Película 2: Tiene 200 reseñas con calificación promedio 4.8
        Pelicula pelicula2 = new Pelicula(2, "Pulp Fiction", "en", 0.0f, new Date());
        ListaEnlazada<Review> reviews2 = new ListaEnlazada<>();
        for (int i = 0; i < 200; i++) {
            reviews2.insertar(new Review(i + 151, 2, 2, 4.8f));
        }
        tablaPeliculas.put(2, pelicula2);
        reviewsPorPelicula.put(2, reviews2);

        // Película 3: Tiene 120 reseñas con calificación promedio 4.6
        Pelicula pelicula3 = new Pelicula(3, "Inception", "en", 0.0f, new Date());
        ListaEnlazada<Review> reviews3 = new ListaEnlazada<>();
        for (int i = 0; i < 120; i++) {
            reviews3.insertar(new Review(i + 351, 3, 3, 4.6f));
        }
        tablaPeliculas.put(3, pelicula3);
        reviewsPorPelicula.put(3, reviews3);

        // Configurar CargaDeDatos con datos simulados
        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        // Ejecutar la funcionalidad
        uMovieImpl.Top_10_de_las_películas_que_mejor_calificación_media_tienen_por_parte_de_los_usuarios();

        // Capturar la salida
        String output = outputStream.toString();

        // Validar resultados
        assert output.contains("1,The Godfather,4.70");
        assert output.contains("2,Pulp Fiction,4.80");
        assert output.contains("3,Inception,4.60");
        assert output.contains("Tiempo de ejecución:");
    }

    //Camino Triste
    @Test
    void testTop10PeliculasConMenosDe100ReviewsT() {
        // Crear una tabla hash de películas con reseñas insuficientes
        TablaHash<Integer, Pelicula> tablaPeliculas = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = new TablaHash<>();

        // Película 1: Tiene 99 reseñas (menos de 100)
        Pelicula pelicula1 = new Pelicula(1, "El Padrino", "es", 0.0f, new Date());
        ListaEnlazada<Review> reviews1 = new ListaEnlazada<>();
        for (int i = 0; i < 99; i++) {
            reviews1.insertar(new Review(i + 1, 1, 1, 4.7f));
        }
        tablaPeliculas.put(1, pelicula1);
        reviewsPorPelicula.put(1, reviews1);

        // Película 2: No tiene reseñas
        Pelicula pelicula2 = new Pelicula(2, "Pulp Fiction", "es", 0.0f, new Date());
        tablaPeliculas.put(2, pelicula2);

        // Película 3: Tiene 101 reseñas (cumple el filtro)
        Pelicula pelicula3 = new Pelicula(3, "Inception", "es", 0.0f, new Date());
        ListaEnlazada<Review> reviews3 = new ListaEnlazada<>();
        for (int i = 0; i < 101; i++) {
            reviews3.insertar(new Review(i + 200, 3, 3, 4.6f));
        }
        tablaPeliculas.put(3, pelicula3);
        reviewsPorPelicula.put(3, reviews3);

        // Configurar CargaDeDatos con datos simulados
        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        // Ejecutar la funcionalidad
        uMovieImpl.Top_10_de_las_películas_que_mejor_calificación_media_tienen_por_parte_de_los_usuarios();

        // Capturar la salida
        String output = outputStream.toString();

        // Validar resultados
        assert !output.contains("1,El Padrino"); // Debe excluir películas con menos de 100 reseñas
        assert !output.contains("2,Pulp Fiction"); // Debe excluir películas sin reseñas
        assert output.contains("3,Inception,4.60"); // Debe incluir películas con más de 100 reseñas
        assert output.contains("Tiempo de ejecución:");
    }

    // ============ TESTS PARA TOP 5 COLECCIONES ============

    @Test
        // Camino Feliz: sagas reales y virtuales
    void testTop5ColeccionesCaminoFeliz() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasColecciones();
        TablaHash<Integer, Saga> tablaSagas = crearTablaSagasColecciones();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagas);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();

        assert output.contains("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
        assert output.contains("Tiempo de ejecución de la consulta:");
        assert output.contains("Saga Marvel"); // La que más ingreso tiene
        assert output.contains("Pelicula Individual A"); // Película individual
    }

    @Test
        // Camino Feliz: solo sagas reales
    void testTop5ColeccionesSoloSagas() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSoloSagas();
        TablaHash<Integer, Saga> tablaSagas = crearTablaSagasVariadas();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagas);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();
        assert output.contains("Saga A"); // Mayor ingreso
        assert output.contains("Saga E"); // Menor ingreso del top 5
    }

    @Test
        // Camino Feliz: solo películas individuales
    void testTop5ColeccionesSoloPeliculasIndividuales() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasIndividuales();
        TablaHash<Integer, Saga> tablaSagas = new TablaHash<>(); // Vacío

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagas);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();
        assert output.contains("Pelicula Individual 1"); // Mayor ingreso individual
        assert output.contains(",1,["); // Cantidad películas = 1 para todas
    }

    @Test
        // Camino Triste: tablas vacías
    void testTop5ColeccionesTablaVacia() {
        TablaHash<Integer, Pelicula> tablaPeliculasVacia = new TablaHash<>();
        TablaHash<Integer, Saga> tablaSagasVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculasVacia, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagasVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();
        assert output.contains("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
        assert output.contains("Tiempo de ejecución de la consulta:");
    }

    @Test
        // Camino Triste: menos de 5 colecciones totales
    void testTop5ColeccionesMenosDe5() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasPocasColecciones();
        TablaHash<Integer, Saga> tablaSagas = crearTablaSagasPocas();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagas);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();
        assert output.contains("Saga Unica");
        assert output.contains("Pelicula Sola");
    }

    @Test
        // Camino Triste: saga con múltiples películas
    void testTop5ColeccionesSagaMultiplesPeliculas() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSagaMultiple();
        TablaHash<Integer, Saga> tablaSagas = crearTablaSagaMultiple();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, new TablaHash<>());
        cargaDatos.setTablaSagas(tablaSagas);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();

        String output = outputStream.toString();
        assert output.contains("Saga Multi");
        assert output.contains(",3,["); // 3 películas en la saga
        assert output.contains("101,102,103"); // IDs de las películas
    }

    @Test
        // Camino Triste: CargaDeDatos retorna null
    void testTop5ColeccionesCargaDatosNull() {
        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(null, null);
        cargaDatos.setTablaSagas(null);
        uMovieImpl = new UMovieimpl(cargaDatos);

        try {
            uMovieImpl.Top_5_de_las_colecciones_que_más_ingresos_generaron();
            assert false : "Debería haber lanzado NullPointerException";
        } catch (NullPointerException e) {
            assert true;
        }
    }

    // ============================ TESTS PARA TOP 10 DIRECTORES ============================

    @Test
        // Camino Feliz: director con más de 100 reviews y varias películas
    void testTop10Directores_CaminoFeliz() {
        TablaHash<String, Director> tablaDirectores = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = new TablaHash<>();

        // Crear películas y reviews para un director
        Director director = new Director("Christopher Nolan");
        ListaEnlazada<Pelicula> peliculas = new ListaEnlazada<>();

        for (int i = 0; i < 3; i++) {
            int id = i + 1;
            Pelicula pelicula = new Pelicula(id, "Pelicula " + id, "en", 0.0f, new Date());
            peliculas.insertar(pelicula);

            ListaEnlazada<Review> reviews = new ListaEnlazada<>();
            for (int j = 0; j < 50; j++) {
                reviews.insertar(new Review(j + id * 100, 1, id, 4.5f));
            }
            reviewsPorPelicula.put(id, reviews);
        }
        director.setPeliculasDirigidas(peliculas);
        tablaDirectores.put("Christopher Nolan", director);

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(new TablaHash<>(), reviewsPorPelicula);
        cargaDatos.setDirectores(tablaDirectores);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_10_de_los_directores_que_mejor_calificación_tienen();

        String output = outputStream.toString();

        assert output.contains("Top 10 de los directores que mejor calificación tienen:");
        assert output.contains("Christopher Nolan");
    }

    @Test
        // Camino Triste: director con solo una película o menos de 100 reviews
    void testTop10Directores_CaminoTriste() {
        TablaHash<String, Director> tablaDirectores = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = new TablaHash<>();

        // Director con una sola película y pocas reviews
        Director director = new Director("Director Solitario");
        ListaEnlazada<Pelicula> peliculas = new ListaEnlazada<>();
        Pelicula pelicula = new Pelicula(1, "Pelicula Unica", "en", 0.0f, new Date());
        peliculas.insertar(pelicula);
        director.setPeliculasDirigidas(peliculas);
        tablaDirectores.put("Director Solitario", director);

        ListaEnlazada<Review> reviews = new ListaEnlazada<>();
        for (int i = 0; i < 10; i++) {
            reviews.insertar(new Review(i + 1, 1, 1, 3.0f));
        }
        reviewsPorPelicula.put(1, reviews);

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(new TablaHash<>(), reviewsPorPelicula);
        cargaDatos.setDirectores(tablaDirectores);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Top_10_de_los_directores_que_mejor_calificación_tienen();

        String output = outputStream.toString();

        assert output.contains("Top 10 de los directores que mejor calificación tienen:");
        assert !output.contains("Director Solitario");
    }
}

    // ============ TESTS PARA ACTOR CON MÁS CALIFICACIONES POR MES ============

    @Test
        // Camino Feliz: actores en diferentes meses
    void testActorPorMesCaminoFeliz() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasActoresMeses();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsActoresMeses();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();

        assert output.contains("Actor con más calificaciones por mes:");
        assert output.contains("Mes           Nombre del actor          Películas  Calificaciones");
        assert output.contains("Tiempo de ejecución:");
        assert output.contains("Tom Holland"); // Enero
        assert output.contains("Emma Stone"); // Febrero
        assert output.contains("Ryan Gosling"); // Marzo
    }

    @Test
        // Camino Triste: sin películas
    void testActorPorMesSinPeliculas() {
        TablaHash<Integer, Pelicula> tablaPeliculasVacia = new TablaHash<>();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculasVacia, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");

        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (String mes : meses) {
            assert output.contains(mes);
        }
        assert output.contains("N/A");
    }

    @Test
        // Camino Triste: películas sin actores
    void testActorPorMesPeliculasSinActores() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSinActores();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsSinActores();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");
        assert output.contains("N/A");
    }

    @Test
        // Camino Triste: películas sin fecha
    void testActorPorMesPeliculasSinFecha() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasSinFecha();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsSinFecha();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");
        assert output.contains("N/A");
    }

    @Test
        // Camino Triste: películas sin reviews
    void testActorPorMesPeliculasSinReviews() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasActoresSinReviews();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsVacia = new TablaHash<>();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsVacia);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");
        assert output.contains("N/A");
    }

    @Test
        // Camino Triste: empate en calificaciones
    void testActorPorMesEmpateCalificaciones() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasEmpate();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsEmpate();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");
        boolean tieneActor = output.contains("Actor A") || output.contains("Actor B");
        assert tieneActor : "Debería mostrar uno de los actores empatados";
    }

    @Test
        // Camino Triste: películas en un solo mes
    void testActorPorMesPeliculasEnUnSoloMes() {
        TablaHash<Integer, Pelicula> tablaPeliculas = crearTablaPeliculasUnSoloMes();
        TablaHash<Integer, ListaEnlazada<Review>> reviewsPorPelicula = crearReviewsUnSoloMes();

        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(tablaPeliculas, reviewsPorPelicula);
        uMovieImpl = new UMovieimpl(cargaDatos);

        uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

        String output = outputStream.toString();
        assert output.contains("Actor con más calificaciones por mes:");
        assert output.contains("Actor Enero");
        assert output.contains("N/A");
    }

    @Test
        // Camino Triste: CargaDeDatos retorna null
    void testActorPorMesCargaDatosNull() {
        CargaDeDatosTest cargaDatos = new CargaDeDatosTest(null, null);
        uMovieImpl = new UMovieimpl(cargaDatos);

        try {
            uMovieImpl.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();
            assert false : "Debería haber lanzado NullPointerException";
        } catch (NullPointerException e) {
            assert true;
        }
    }

    // ============ MÉTODOS AUXILIARES PARA TESTS DE PELÍCULAS POR IDIOMA ============

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

    // ============ MÉTODOS AUXILIARES PARA TESTS DE COLECCIONES ============

    private TablaHash<Integer, Pelicula> crearTablaPeliculasColecciones() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        // Películas que pertenecen a sagas
        tabla.put(1, crearPeliculaConSaga(1, "Iron Man", 100, 500000000)); // Saga Marvel
        tabla.put(2, crearPeliculaConSaga(2, "Avengers", 100, 1000000000)); // Saga Marvel
        tabla.put(3, crearPeliculaConSaga(3, "Harry Potter 1", 200, 800000000)); // Saga HP

        // Películas individuales (id_saga = -1)
        tabla.put(4, crearPeliculaIndividual(4, "Pelicula Individual A", 2000000000));
        tabla.put(5, crearPeliculaIndividual(5, "Pelicula Individual B", 300000000));

        return tabla;
    }

    private TablaHash<Integer, Saga> crearTablaSagasColecciones() {
        TablaHash<Integer, Saga> tabla = new TablaHash<>();

        // Saga Marvel (ID 100) - Mayor ingreso
        tabla.put(100, new Saga(100, "Saga Marvel", 2, 1500000000));

        // Saga Harry Potter (ID 200) - Menor ingreso
        tabla.put(200, new Saga(200, "Saga Harry Potter", 1, 800000000));

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasSoloSagas() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        tabla.put(1, crearPeliculaConSaga(1, "Pelicula Saga A", 100, 0));
        tabla.put(2, crearPeliculaConSaga(2, "Pelicula Saga B", 200, 0));
        tabla.put(3, crearPeliculaConSaga(3, "Pelicula Saga C", 300, 0));

        return tabla;
    }

    private TablaHash<Integer, Saga> crearTablaSagasVariadas() {
        TablaHash<Integer, Saga> tabla = new TablaHash<>();

        tabla.put(100, new Saga(100, "Saga A", 1, 1000000000)); // Mayor
        tabla.put(200, new Saga(200, "Saga B", 1, 900000000));
        tabla.put(300, new Saga(300, "Saga C", 1, 800000000));
        tabla.put(400, new Saga(400, "Saga D", 1, 700000000));
        tabla.put(500, new Saga(500, "Saga E", 1, 600000000)); // Menor
        tabla.put(600, new Saga(600, "Saga F", 1, 500000000)); // No debería aparecer

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasIndividuales() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        tabla.put(1, crearPeliculaIndividual(1, "Pelicula Individual 1", 1000000000));
        tabla.put(2, crearPeliculaIndividual(2, "Pelicula Individual 2", 900000000));
        tabla.put(3, crearPeliculaIndividual(3, "Pelicula Individual 3", 800000000));
        tabla.put(4, crearPeliculaIndividual(4, "Pelicula Individual 4", 700000000));
        tabla.put(5, crearPeliculaIndividual(5, "Pelicula Individual 5", 600000000));
        tabla.put(6, crearPeliculaIndividual(6, "Pelicula Individual 6", 500000000)); // No debería aparecer

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasPocasColecciones() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        tabla.put(1, crearPeliculaConSaga(1, "Pelicula de Saga", 100, 0));
        tabla.put(2, crearPeliculaIndividual(2, "Pelicula Sola", 500000000));

        return tabla;
    }

    private TablaHash<Integer, Saga> crearTablaSagasPocas() {
        TablaHash<Integer, Saga> tabla = new TablaHash<>();
        tabla.put(100, new Saga(100, "Saga Unica", 1, 600000000));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasSagaMultiple() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        tabla.put(101, crearPeliculaConSaga(101, "Pelicula Multi 1", 100, 0));
        tabla.put(102, crearPeliculaConSaga(102, "Pelicula Multi 2", 100, 0));
        tabla.put(103, crearPeliculaConSaga(103, "Pelicula Multi 3", 100, 0));

        return tabla;
    }

    private TablaHash<Integer, Saga> crearTablaSagaMultiple() {
        TablaHash<Integer, Saga> tabla = new TablaHash<>();
        tabla.put(100, new Saga(100, "Saga Multi", 3, 1500000000));
        return tabla;
    }

    // ============ MÉTODOS AUXILIARES PARA TESTS DE ACTORES POR MES ============

    private TablaHash<Integer, Pelicula> crearTablaPeliculasActoresMeses() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        // Película de enero con Tom Holland
        tabla.put(1, crearPeliculaConActorYFecha(1, "Spider-Man No Way Home",
                crearFecha(2024, Calendar.JANUARY, 15), crearListaActores("Tom Holland")));

        // Película de febrero con Emma Stone
        tabla.put(2, crearPeliculaConActorYFecha(2, "La La Land",
                crearFecha(2024, Calendar.FEBRUARY, 20), crearListaActores("Emma Stone")));

        // Película de marzo con Ryan Gosling
        tabla.put(3, crearPeliculaConActorYFecha(3, "Blade Runner",
                crearFecha(2024, Calendar.MARCH, 10), crearListaActores("Ryan Gosling")));

        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsActoresMeses() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();

        // Tom Holland tiene más reviews en enero
        tabla.put(1, crearListaReviews(10));

        // Emma Stone domina febrero
        tabla.put(2, crearListaReviews(8));

        // Ryan Gosling domina marzo
        tabla.put(3, crearListaReviews(6));

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasSinActores() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        Pelicula pelicula = new Pelicula();
        pelicula.setId(1);
        pelicula.setTitulo("Película Sin Actores");
        pelicula.setFecha_publicacion(crearFecha(2024, Calendar.JANUARY, 15));
        pelicula.setActores(null); // Sin actores

        tabla.put(1, pelicula);
        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsSinActores() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();
        tabla.put(1, crearListaReviews(5));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasSinFecha() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        Pelicula pelicula = new Pelicula();
        pelicula.setId(1);
        pelicula.setTitulo("Película Sin Fecha");
        pelicula.setFecha_publicacion(null); // Sin fecha
        pelicula.setActores(crearListaActores("Actor Sin Fecha"));

        tabla.put(1, pelicula);
        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsSinFecha() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();
        tabla.put(1, crearListaReviews(5));
        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasActoresSinReviews() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        tabla.put(1, crearPeliculaConActorYFecha(1, "Película Sin Reviews",
                crearFecha(2024, Calendar.JANUARY, 15), crearListaActores("Actor Sin Reviews")));

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasEmpate() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        // Dos películas en enero con diferentes actores
        tabla.put(1, crearPeliculaConActorYFecha(1, "Película A",
                crearFecha(2024, Calendar.JANUARY, 15), crearListaActores("Actor A")));

        tabla.put(2, crearPeliculaConActorYFecha(2, "Película B",
                crearFecha(2024, Calendar.JANUARY, 20), crearListaActores("Actor B")));

        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsEmpate() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();

        // Ambos actores tienen la misma cantidad de reviews
        tabla.put(1, crearListaReviews(5));
        tabla.put(2, crearListaReviews(5));

        return tabla;
    }

    private TablaHash<Integer, Pelicula> crearTablaPeliculasUnSoloMes() {
        TablaHash<Integer, Pelicula> tabla = new TablaHash<>();

        // Todas las películas solo en enero
        tabla.put(1, crearPeliculaConActorYFecha(1, "Película Enero 1",
                crearFecha(2024, Calendar.JANUARY, 15), crearListaActores("Actor Enero")));

        tabla.put(2, crearPeliculaConActorYFecha(2, "Película Enero 2",
                crearFecha(2024, Calendar.JANUARY, 20), crearListaActores("Otro Actor")));

        return tabla;
    }

    private TablaHash<Integer, ListaEnlazada<Review>> crearReviewsUnSoloMes() {
        TablaHash<Integer, ListaEnlazada<Review>> tabla = new TablaHash<>();

        // Actor Enero tiene más reviews
        tabla.put(1, crearListaReviews(10));
        tabla.put(2, crearListaReviews(5));

        return tabla;
    }

    // ============ MÉTODOS AUXILIARES COMUNES ============

    private Pelicula crearPelicula(int id, String titulo, String idioma) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setIdioma_original(idioma);
        return pelicula;
    }

    private Pelicula crearPeliculaConSaga(int id, String titulo, int idSaga, int ingreso) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setId_saga(idSaga);
        pelicula.setIngreso(ingreso);
        return pelicula;
    }

    private Pelicula crearPeliculaIndividual(int id, String titulo, int ingreso) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setId_saga(-1); // Película individual
        pelicula.setIngreso(ingreso);
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

    // Métodos auxiliares para crear datos específicos de actores

    private Pelicula crearPeliculaConActorYFecha(int id, String titulo, Date fecha, ListaEnlazada<Actor> actores) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setFecha_publicacion(fecha);
        pelicula.setActores(actores);
        return pelicula;
    }

    private Date crearFecha(int año, int mes, int dia) {
        Calendar cal = Calendar.getInstance();
        cal.set(año, mes, dia);
        return cal.getTime();
    }

    private ListaEnlazada<Actor> crearListaActores(String... nombres) {
        ListaEnlazada<Actor> lista = new ListaEnlazada<>();
        for (String nombre : nombres) {
            Actor actor = new Actor();
            actor.setNombre(nombre);
            lista.insertar(actor);
        }
        return lista;
    }

    // Restaurar System.out después de cada test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
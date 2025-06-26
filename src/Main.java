import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        CargaDeDatos cargador = new CargaDeDatos();

        boolean cargaExitosa = false;
        boolean salir = false;

        while (!salir) {
            System.out.println("Seleccione la opción que desee:");
            System.out.println("1. Carga de datos");
            System.out.println("2. Ejecutar consultas");
            System.out.println("3. Salir");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    try {
                        long inicio = System.currentTimeMillis();

                        int peliculasCargadas = cargador.cargarPeliculas("resources/movies_metadata.csv");
                        int ratingsCargados = cargador.cargarRatings("resources/ratings_1mm.csv");
                        int creditosCargados = cargador.cargarCreditos("resources/credits.csv");

                        if (peliculasCargadas > 0 && ratingsCargados > 0 && creditosCargados > 0) {
                            contarTiempoCarga(inicio,"Carga de datos exitosa,tiempo de carga: ");
                            cargaExitosa = true;
                        } else {
                            System.err.println(" Carga fallida.");
                        }
                    } catch (Exception e) {
                        System.out.println(" Error al cargar los datos: " + e.getMessage());
                    }
                    break;

                case "2":
                    mostrarMenuConsultas(scanner);
                    break;
                case "3":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }

        scanner.close();
    }

    private static void mostrarMenuConsultas(Scanner scanner) {
        boolean volver = false;
        CargaDeDatos cargaDeDatos = new CargaDeDatos();

        cargaDeDatos.cargarPeliculas("resources/movies_metadata.csv");
        cargaDeDatos.cargarRatings("resources/ratings_1mm.csv");
        cargaDeDatos.cargarCreditos("resources/credits.csv");


        UMovieimpl umovie = new UMovieimpl(cargaDeDatos);


        while (!volver) {
            System.out.println("1. Top 5 de las películas que más calificaciones por idioma.");
            System.out.println("2. Top 10 de las películas que mejor calificación media tienen por parte de los usuarios.");
            System.out.println("3. Top 5 de las colecciones que más ingresos generaron.");
            System.out.println("4. Top 10 de los directores que mejor calificación tienen.");
            System.out.println("5. Actor con más calificaciones recibidas en cada mes del año.");
            System.out.println("6. Usuarios con más calificaciones por género");
            System.out.println("7. Salir");

            String opcionConsulta = scanner.nextLine();

            switch (opcionConsulta) {
                case "1":

                    umovie.Top_5_de_las_películas_que_más_calificaciones_por_idioma();

                    break;
                case "2":
                    long inicio = System.currentTimeMillis();
                    umovie.Top_10_de_las_películas_que_mejor_calificación_media_tienen_por_parte_de_los_usuarios();
                    contarTiempoCarga(inicio,"Tiempo de ejecución de la consulta:");
                    break;
                case "3":

                    umovie.Top_5_de_las_colecciones_que_más_ingresos_generaron();

                    break;
                case "4":
                    inicio = System.currentTimeMillis();
                    umovie.Top_10_de_los_directores_que_mejor_calificación_tienen();
                    contarTiempoCarga(inicio,"Tiempo de ejecución de la consulta:");
                    break;
                case "5":

                    umovie.Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año();

                    break;
                case "6":

                    umovie.Usuarios_con_más_calificaciones_por_género();

                    break;
                case "7":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }

    private static void contarTiempoCarga(long tiempoInicial, String mensaje){
        long tiempoFinal = System.currentTimeMillis();

        long tiempoCarga = tiempoFinal - tiempoInicial;

        System.out.println(mensaje + tiempoCarga + "ms");
    }
}
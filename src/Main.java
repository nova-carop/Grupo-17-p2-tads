import entidades.Pelicula;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<Pelicula> peliculas = null;
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
                        peliculas = CargaDatos.cargarPeliculas();
                        long fin = System.currentTimeMillis();
                        System.out.println("Carga de datos exitosa, tiempo de ejecución de la carga: " );

                    } catch (Exception e) {
                        System.out.println("Error al cargar los datos: " + e.getMessage());
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
                    // LLAMADA A LA LOGICA DE LA FUNCION 1
                    System.out.println("<id_pelicula>, <titulo_pelicula>,<total_calificaciones>,<idioma>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    break;
                case "2":
                    // LLAMADA A LA LOGICA DE LA FUNCION 2
                    System.out.println("<id_pelicula>, <titulo_pelicula>,<calificacion_media>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    break;
                case "3":
                    System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    break;
                case "4":
                    System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    break;
                case "5":
                    System.out.println("<mes>,<nombre_actor>,<cantidad_peliculas>,<cantidad_de_calificaciones>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    break;
                case "6":
                    System.out.println("<id_usuario>,<genero>,<cantidad_de_calificaciones_sobre_ese_genero>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
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
}
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        long tiempoInicial;
        boolean salir = false;

        while (!salir) {
            System.out.println("Seleccione la opción que desee:");
            System.out.println("1. Carga de datos");
            System.out.println("2. Ejecutar consultas");
            System.out.println("3. Salir");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    //FALTA PONER LA CARGA DE DATOS ACA
                    tiempoInicial = System.currentTimeMillis();
                    Thread.sleep(345);
                    contarTiempoCarga(tiempoInicial,"Carga de datos exitosa, tiempo de ejecución de la carga:");
                    break;
                case "2":
                    tiempoInicial = System.currentTimeMillis();
                    mostrarMenuConsultas(scanner,tiempoInicial);
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

    private static void mostrarMenuConsultas(Scanner scanner, long tiempoInicial) {
        boolean volver = false;
        contarTiempoCarga(tiempoInicial, "Tiempo de ejecución de la consulta:");
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
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_pelicula>, <titulo_pelicula>,<total_calificaciones>,<idioma>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "2":
                    // LLAMADA A LA LOGICA DE LA FUNCION 2
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_pelicula>, <titulo_pelicula>,<calificacion_media>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "3":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "4":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "5":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<mes>,<nombre_actor>,<cantidad_peliculas>,<cantidad_de_calificaciones>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "6":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_usuario>,<genero>,<cantidad_de_calificaciones_sobre_ese_genero>");
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
                    break;
                case "7":
                    tiempoInicial = System.currentTimeMillis();
                    volver = true;
                    contarTiempoCarga(tiempoInicial,"Tiempo de ejecución de la consulta:");
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
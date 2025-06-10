import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        long tiempoInicial = System.currentTimeMillis();
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
                    System.out.println("Carga de datos exitosa, tiempo de ejecución de la carga: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "2":
                    tiempoInicial = System.currentTimeMillis();
                    mostrarMenuConsultas(scanner,tiempoInicial);
                    break;
                case "3":
                    salir = true;
                    break;
                default:
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("Opción no válida. Intente nuevamente.");
                    contarTiempoCarga(tiempoInicial);
                    break;
            }
        }

        scanner.close();
    }

    private static void mostrarMenuConsultas(Scanner scanner, long tiempoInicial) {
        boolean volver = false;

        while (!volver) {
            contarTiempoCarga(tiempoInicial);
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
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "2":
                    // LLAMADA A LA LOGICA DE LA FUNCION 2
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_pelicula>, <titulo_pelicula>,<calificacion_media>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "3":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "4":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "5":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<mes>,<nombre_actor>,<cantidad_peliculas>,<cantidad_de_calificaciones>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "6":
                    tiempoInicial = System.currentTimeMillis();
                    System.out.println("<id_usuario>,<genero>,<cantidad_de_calificaciones_sobre_ese_genero>");
                    System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");
                    contarTiempoCarga(tiempoInicial);
                    break;
                case "7":
                    tiempoInicial = System.currentTimeMillis();
                    volver = true;
                    contarTiempoCarga(tiempoInicial);
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }

    private static void contarTiempoCarga(long tiempoInicial){
        long tiempoFinal = System.currentTimeMillis();

        long tiempoCarga = tiempoFinal - tiempoInicial;
        System.out.println("Tiempo de carga:" + tiempoCarga);
    }
}
public class MedicionPerformanceCarga {

    public static void main(String[] args) {
        // Rutas de tus archivos
        String rutaPeliculas = "resources/movies_metadata.csv";
        String rutaRatings = "resources/ratings_1mm.csv";
        String rutaCreditos = "resources/credits.csv";

        int numeroEjecuciones = 5; // Número de veces que ejecutarás la carga

        medirCargaCompleta(rutaPeliculas, rutaRatings, rutaCreditos, numeroEjecuciones);

        System.out.println("\n" + "=".repeat(60));


    }

    public static void medirCargaCompleta(String rutaPeliculas, String rutaRatings,
                                          String rutaCreditos, int ejecuciones) {
        System.out.println("MEDICIÓN DE CARGA COMPLETA");
        System.out.println("=".repeat(50));

        long[] tiempos = new long[ejecuciones];

        for (int i = 0; i < ejecuciones; i++) {
            System.out.println("\nEjecución " + (i + 1) + ":");

            long inicioTotal = System.currentTimeMillis();

            // Crear nueva instancia para cada ejecución
            CargaDeDatos carga = new CargaDeDatos();

            // Medir carga de películas
            long inicioPeliculas = System.currentTimeMillis();
            int peliculasCargadas = carga.cargarPeliculas(rutaPeliculas);
            long finPeliculas = System.currentTimeMillis();

            // Medir carga de ratings
            long inicioRatings = System.currentTimeMillis();
            int ratingsCargados = carga.cargarRatings(rutaRatings);
            long finRatings = System.currentTimeMillis();

            // Medir carga de créditos
            long inicioCreditos = System.currentTimeMillis();
            int creditosCargados = carga.cargarCreditos(rutaCreditos);
            long finCreditos = System.currentTimeMillis();

            long finTotal = System.currentTimeMillis();

            long tiempoTotal = finTotal - inicioTotal;
            tiempos[i] = tiempoTotal;

            System.out.printf("  Películas: %d registros en %d ms%n",
                    peliculasCargadas, (finPeliculas - inicioPeliculas));
            System.out.printf("  Ratings: %d registros en %d ms%n",
                    ratingsCargados, (finRatings - inicioRatings));
            System.out.printf("  Créditos: %d registros en %d ms%n",
                    creditosCargados, (finCreditos - inicioCreditos));
            System.out.printf("  TOTAL: %d ms%n", tiempoTotal);

            // Limpiar memoria entre ejecuciones
            carga = null;
            System.gc();

            // Pausa pequeña entre ejecuciones
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Calcular estadísticas
        calcularEstadisticas("CARGA COMPLETA", tiempos);
    }

    public static void medirCargaIndividual(String rutaPeliculas, String rutaRatings,
                                            String rutaCreditos, int ejecuciones) {
        System.out.println("MEDICIÓN DE CARGA POR ARCHIVO");
        System.out.println("=".repeat(50));

        // Medir películas
        medirArchivoIndividual("PELÍCULAS", rutaPeliculas, ejecuciones, "peliculas");

        // Medir ratings (necesita películas cargadas)
        medirArchivoIndividual("RATINGS", rutaRatings, ejecuciones, "ratings");

        // Medir créditos (necesita películas cargadas)
        medirArchivoIndividual("CRÉDITOS", rutaCreditos, ejecuciones, "creditos");
    }

    private static void medirArchivoIndividual(String nombre, String ruta,
                                               int ejecuciones, String tipo) {
        System.out.println("\n" + nombre + ":");
        System.out.println("-".repeat(30));

        long[] tiempos = new long[ejecuciones];

        for (int i = 0; i < ejecuciones; i++) {
            CargaDeDatos carga = new CargaDeDatos();

            // Para ratings y créditos necesitamos cargar películas primero
            if (!tipo.equals("peliculas")) {
                carga.cargarPeliculas("resources/movies_metadata.csv");
            }

            long inicio = System.currentTimeMillis();

            int registros = 0;
            switch (tipo) {
                case "peliculas":
                    registros = carga.cargarPeliculas(ruta);
                    break;
                case "ratings":
                    registros = carga.cargarRatings(ruta);
                    break;
                case "creditos":
                    registros = carga.cargarCreditos(ruta);
                    break;
            }

            long fin = System.currentTimeMillis();
            long tiempo = fin - inicio;
            tiempos[i] = tiempo;

            System.out.printf("  Ejecución %d: %d registros en %d ms%n",
                    (i + 1), registros, tiempo);

            // Limpiar memoria
            carga = null;
            System.gc();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        calcularEstadisticas(nombre, tiempos);
    }

    private static void calcularEstadisticas(String nombre, long[] tiempos) {
        // Calcular promedio
        long suma = 0;
        for (long tiempo : tiempos) {
            suma += tiempo;
        }
        double promedio = (double) suma / tiempos.length;

        // Calcular mínimo y máximo
        long minimo = tiempos[0];
        long maximo = tiempos[0];
        for (long tiempo : tiempos) {
            if (tiempo < minimo) minimo = tiempo;
            if (tiempo > maximo) maximo = tiempo;
        }

        // Calcular desviación estándar
        double sumaCuadrados = 0;
        for (long tiempo : tiempos) {
            sumaCuadrados += Math.pow(tiempo - promedio, 2);
        }
        double desviacion = Math.sqrt(sumaCuadrados / tiempos.length);

        System.out.println("\nESTADÍSTICAS " + nombre + ":");
        System.out.printf("  Promedio: %.2f ms%n", promedio);
        System.out.printf("  Mínimo: %d ms%n", minimo);
        System.out.printf("  Máximo: %d ms%n", maximo);
        System.out.printf("  Desviación estándar: %.2f ms%n", desviacion);

        // Mostrar todos los tiempos
        System.out.print("  Tiempos individuales: ");
        for (int i = 0; i < tiempos.length; i++) {
            System.out.print(tiempos[i] + "ms");
            if (i < tiempos.length - 1) System.out.print(", ");
        }
        System.out.println();
    }
}
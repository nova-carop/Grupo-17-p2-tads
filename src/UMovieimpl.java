import interfaces.UMovieMgt;

public class UMovieimpl implements UMovieMgt {
    @Override
    public void Top_5_de_las_películas_que_más_calificaciones_por_idioma() {
        //para no olvidar de que devuelve la funcion
        System.out.println("<id_pelicula>, <titulo_pelicula>,<total_calificaciones>,<idioma>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Top_10_de_las_películas_que_mejor_calificación_media_tienen_por_parte_de_los_usuarios() {

    }

    @Override
    public void Top_5_de_las_colecciones_que_más_ingresos_generaron() {
        System.out.println("<id_coleccion>,<titulo_coleccion>,<cantidad_peliculas>,[id_p1,id_p2],<ingreso_generado>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Top_10_de_los_directores_que_mejor_calificación_tienen() {
        System.out.println("<nombre_director>,<cantidad_peliculas>,<mediana_calificacion>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Actor_con_más_calificaciones_recibidas_en_cada_mes_del_año() {
        System.out.println("<mes>,<nombre_actor>,<cantidad_peliculas>,<cantidad_de_calificaciones>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }

    @Override
    public void Usuarios_con_más_calificaciones_por_género() {
        System.out.println("<id_usuario>,<genero>,<cantidad_de_calificaciones_sobre_ese_genero>");
        System.out.println("Tiempo de ejecución de la consulta: <tiempo_ejecucion>");

    }
}

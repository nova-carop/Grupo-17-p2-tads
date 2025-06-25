package interfaces;

public interface MyList<T> {

    void insertar(T elemento);
    void insertarAlFinal(T elemento);


    T obtener(int posicion);
    boolean contiene(T elemento);
    int tamanio();
    boolean estaVacia();


    void clear();
}
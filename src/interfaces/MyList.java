package interfaces;



public interface MyList<T> {
    void insertar(T elemento);
    void insertar(int posicion, T elemento);
    T obtener(int posicion);
    boolean contiene(T elemento);
    int tamanio();
    boolean estaVacia();
}


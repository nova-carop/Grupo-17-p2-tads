package interfaces;



public interface MyHash<K, T> {
    void insertar(K clave, T valor);
    T obtener(K clave);
    boolean contieneClave(K clave);
    void eliminar(K clave);
    int tamanio();
}


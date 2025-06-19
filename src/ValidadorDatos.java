import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidadorDatos {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Integer validarEntero(String valor) {
        if (estaVacio(valor)) return null;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Float validarFloat(String valor) {
        if (estaVacio(valor)) return null;
        try {
            return Float.parseFloat(valor.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Date validarFecha(String fecha) {
        if (estaVacio(fecha)) return null;
        try {
            dateFormat.setLenient(false);
            return dateFormat.parse(fecha.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidadorCsv {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean esCampoVacio(String campo) {
        return campo == null || campo.trim().isEmpty();
    }

    public static Integer parsearEntero(String campo) throws NumberFormatException {
        return Integer.parseInt(campo.trim());
    }

    public static Float parsearFloat(String campo) throws NumberFormatException {
        return Float.parseFloat(campo.trim());
    }

    public static Date parsearFecha(String campo) throws ParseException {
        return sdf.parse(campo.trim());
    }
}


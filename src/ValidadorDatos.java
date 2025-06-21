import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidadorDatos {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

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

    public static boolean esCadenaValida(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static String limpiarYValidarJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "[]";
        }

        String cleaned = json.replace("'", "\"")
                .replace(": None", ": null")
                .replace(":None", ":null")
                .replace(": False", ": false")
                .replace(":False", ":false")
                .replace(": True", ": true")
                .replace(":True", ":true")
                .replace("\\\"", "'")
                .replace("\"{", "{")
                .replace("}\"", "}");

        if (!cleaned.startsWith("[") && !cleaned.startsWith("{")) {
            cleaned = "[" + cleaned + "]";
        }


        try {
            jsonMapper.readTree(cleaned);
            return cleaned;
        } catch (JsonProcessingException e) {
            return "[]"; // Retorna array vacío si no se puede parsear
        }
    }

}
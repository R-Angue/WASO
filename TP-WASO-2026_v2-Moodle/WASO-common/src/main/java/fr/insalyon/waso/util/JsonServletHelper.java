package fr.insalyon.waso.util;

import fr.insalyon.waso.util.exception.ServiceException;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author WASO Team
 */
public class JsonServletHelper {

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String CONTENTTYPE_JSON = "application/json";

    public static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat JSON_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void printJsonOutput(HttpServletResponse response, JsonObjectBuilder container) throws IOException {

        response.setContentType(CONTENTTYPE_JSON);
        response.setCharacterEncoding(ENCODING_UTF8);
        
        Map<String, Object> jsonWriterProperties = new HashMap<>(1);
        jsonWriterProperties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriter jsonWriter = Json.createWriterFactory(jsonWriterProperties).createWriter(response.getWriter());
        jsonWriter.writeObject(container.build());
        jsonWriter.close();
    }

    public static ServiceException ServiceObjectMetierExecutionException(String bloc, String som, String error) {
        return new ServiceException("Error in SOM " + bloc + "::" + som + ": " + error);
    }

    public static ServiceException ServiceObjectMetierExecutionException(String bloc, String som, String error, Exception ex) {
        return new ServiceException("Exception in SOM " + bloc + "::" + som + ": " + error, ex);
    }

    public static ServiceException ServiceObjectMetierExecutionException(String bloc, String som, Exception ex) {
        return new ServiceException("Exception in SOM " + bloc + "::" + som, ex);
    }

    public static ServiceException ServiceMetierExecutionException(String sma, String error) {
        return new ServiceException("Error in SMA " + sma + ": " + error);
    }

    public static ServiceException ServiceMetierExecutionException(String sma, String error, Exception ex) {
        return new ServiceException("Exception in SMA " + sma + ": " + error, ex);
    }

    public static ServiceException ServiceMetierExecutionException(String sma, Exception ex) {
        return new ServiceException("Exception in SMA " + sma, ex);
    }

    public static ServiceException ActionExecutionException(String action, String error) {
        return new ServiceException("Error in AJAX Action " + action + ": " + error);
    }
    
    public static ServiceException ActionExecutionException(String action, Exception ex) {
        return new ServiceException("Exception in AJAX Action " + action, ex);
    }
    
    public static ServiceException ActionExecutionException(String action, String error, Exception ex) {
        return new ServiceException("Exception in AJAX Action " + action + ": " + error, ex);
    }

    
    public static ServiceException ServiceObjectMetierCallException(String url, String bloc, String som) {
        return new ServiceException("Error with call to SOM " + bloc + "::" + som + " [" + url + "]");
    }
    
    public static ServiceException ServiceObjectMetierCallException(String url, String bloc, String som, Exception ex) {
        return new ServiceException("Error with call to SOM " + bloc + "::" + som + " [" + url + "]", ex);
    }

    public static ServiceException ServiceMetierCallException(String url, String sma) {
        return new ServiceException("Error with call to SMA " + sma + " [" + url + "]");
    }

    public static ServiceException ServiceMetierCallException(String url, String sma, Exception ex) {
        return new ServiceException("Error with call to SMA " + sma + " [" + url + "]", ex);
    }

}

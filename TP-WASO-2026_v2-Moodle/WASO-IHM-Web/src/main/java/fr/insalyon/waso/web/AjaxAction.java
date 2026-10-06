package fr.insalyon.waso.web;

import fr.insalyon.waso.util.JsonHttpClient;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.ServiceException;
import fr.insalyon.waso.util.exception.ServiceIOException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 *
 * @author WASO Team
 */
public class AjaxAction {

    protected String smaUrl;
    protected JsonObjectBuilder container;

    protected JsonHttpClient jsonHttpClient;

    protected static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd/MM/YYYY");
    protected static final SimpleDateFormat FULL_DATETIME_FORMAT = new SimpleDateFormat("dd/MM/YYYY @ HH'h'mm");

    public AjaxAction(String smaUrl, JsonObjectBuilder container) {
        this.smaUrl = smaUrl;
        this.container = container;

        this.jsonHttpClient = new JsonHttpClient();
    }

    public void release() {
        try {
            this.jsonHttpClient.close();
        } catch (IOException ex) {
            // Ignorer
        }
    }

    protected static JsonObjectBuilder transformClient(JsonObject client) {

        JsonObjectBuilder jsonItem = Json.createObjectBuilder();

        jsonItem.add("id", client.getJsonNumber("id").toString());
        jsonItem.add("denomination", client.getString("denomination"));

        String ville = client.getString("ville");
        int indexCodePostal = ville.lastIndexOf(" ");
        if (indexCodePostal > 0) {
            ville = ville.substring(indexCodePostal + 1) + " " + ville.substring(0, indexCodePostal);

        }

        jsonItem.add("adresse", client.getString("adresse"));
        jsonItem.add("ville", ville);

        if (client.containsKey("personnes")) {

            JsonArrayBuilder persons = Json.createArrayBuilder();

            for (JsonValue p : client.get("personnes").asJsonArray()) {

                JsonObject person = p.asJsonObject();

                JsonObjectBuilder jsonSubItem = Json.createObjectBuilder();
                jsonSubItem.add("id", person.getJsonNumber("id").toString());
                jsonSubItem.add("nom", person.getString("nom"));
                jsonSubItem.add("prenom", person.getString("prenom"));

                persons.add(jsonSubItem);
            }

            jsonItem.add("personnes", persons);
        }

        return jsonItem;
    }

    protected static JsonArrayBuilder transformListeClient(JsonArray liste) {

        JsonArrayBuilder jsonListe = Json.createArrayBuilder();

        for (JsonValue i : liste) {

            jsonListe.add(transformClient(i.asJsonObject()));
        }

        return jsonListe;
    }

    public void getListeClient() throws ServiceException {
        try {
            JsonObject smaResultContainer = null;
            try {
                smaResultContainer = this.jsonHttpClient.post(
                        this.smaUrl,
                        new JsonHttpClient.Parameter("SMA", "getListeClient")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceMetierCallException(this.smaUrl, "getListeClient", ex);
            }

            JsonArrayBuilder jsonListe = transformListeClient(smaResultContainer.getJsonArray("clients"));

            this.container.add("clients", jsonListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ActionExecutionException("getListeClient", ex);
        }
    }

    public void rechercherClientParNumero(Integer numero) throws ServiceException {
        try {
            JsonObject smaResultContainer = null;
            try {
                smaResultContainer = this.jsonHttpClient.post(
                        this.smaUrl,
                        new JsonHttpClient.Parameter("SMA", "rechercherClientParNumero"),
                        new JsonHttpClient.Parameter("numero", Integer.toString(numero))
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceMetierCallException(this.smaUrl, "rechercherClientParNumero", ex);
            }

            if (smaResultContainer.containsKey("clients")) {
            
                JsonArrayBuilder jsonListe = transformListeClient(smaResultContainer.getJsonArray("clients"));

                this.container.add("clients", jsonListe);
            }

        } catch (IOException ex) {
            throw JsonServletHelper.ActionExecutionException("rechercherClientParNumero", ex);
        }
    }

    void rechercherClientParDenomination(String denomination, String ville) throws ServiceException {
        try {
            JsonObject smaResultContainer = null;
            try {
                smaResultContainer = this.jsonHttpClient.post(
                        this.smaUrl,
                        new JsonHttpClient.Parameter("SMA", "rechercherClientParDenomination"),
                        new JsonHttpClient.Parameter("denomination", denomination),
                        new JsonHttpClient.Parameter("ville", ville)
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceMetierCallException(this.smaUrl, "rechercherClientParNumero", ex);
            }

            if (smaResultContainer.containsKey("clients")) {
            
                JsonArrayBuilder jsonListe = transformListeClient(smaResultContainer.getJsonArray("clients"));

                this.container.add("clients", jsonListe);
            }

        } catch (IOException ex) {
            throw JsonServletHelper.ActionExecutionException("rechercherClientParNumero", ex);
        }  
    }

    void rechercherClientParNomPersonne(String nomPersonne, String ville) throws ServiceException {
        
        // ...
    }

}

package fr.insalyon.waso.sma;

import fr.insalyon.waso.util.JsonHttpClient;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.ServiceException;
import fr.insalyon.waso.util.exception.ServiceIOException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author WASO Team
 */
public class ServiceMetier {

    protected final String somClientUrl;
    protected final String somPersonneUrl;
    protected final String somContactUrl;
    protected final String somStructureUrl;
    protected final String somProduitUrl;
    protected final String somContratUrl;
    protected final JsonObjectBuilder container;

    protected JsonHttpClient jsonHttpClient;

    public ServiceMetier(String somClientUrl, String somPersonneUrl, String somContactUrl, String somStructureUrl, String somProduitUrl, String somContratUrl, JsonObjectBuilder container) {
        this.somClientUrl = somClientUrl;
        this.somPersonneUrl = somPersonneUrl;
        this.somContactUrl = somContactUrl;
        this.somStructureUrl = somStructureUrl;
        this.somProduitUrl = somProduitUrl;
        this.somContratUrl = somContratUrl;
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
    
    public void rechercherClientParDenomination(String denomination, String ville) throws ServiceException {
        try {

            // 1. Rechercher clients par dénomination
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "rechercherClientParDenomination"),
                        new JsonHttpClient.Parameter("denomination", denomination),
                        new JsonHttpClient.Parameter("ville", ville)
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "rechercherClientParDenomination", ex);
            }

            JsonArray inputClientListe = clientContainer.getJsonArray("clients");


            // 2. Obtenir la liste des Personnes
            
            JsonArrayBuilder outputClientListe = Json.createArrayBuilder();

            for (JsonObject client : clientContainer.getJsonArray("clients").getValuesAs(JsonObject.class)) {
                
                JsonObjectBuilder outputClient = Json.createObjectBuilder(client);
                
                JsonArrayBuilder outputPersonnes = Json.createArrayBuilder();

                for (JsonNumber personne_ID : client.getJsonArray("personnes-ID").getValuesAs(JsonNumber.class)) {
                    JsonObject personneContainer = null;
                    try {
                        personneContainer = this.jsonHttpClient.post(
                                this.somPersonneUrl,
                                new JsonHttpClient.Parameter("SOM", "getPersonneParId"),
                                new JsonHttpClient.Parameter("id-personne", personne_ID.toString())
                        );
                    }
                    catch (ServiceIOException ex) {
                        throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
                    }
                    System.out.println(personne_ID);
                   
                    outputPersonnes.add(personneContainer);
                }
                
                outputClient.add("personnes", outputPersonnes);
                outputClientListe.add(outputClient);
            }

            this.container.add("clients", outputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("getListeClient", ex);
        }
    }

    public void getListeClient() throws ServiceException {
        try {

            // 1. Obtenir la liste des Clients
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "getListeClient")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "getListeClient", ex);
            }

            JsonArray inputClientListe = clientContainer.getJsonArray("clients");


            // 2. Obtenir la liste des Personnes
            
            JsonObject personneContainer = null;
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "getListePersonne")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
            }


            // 3. Indexer la liste des Personnes
            
            HashMap<Integer, JsonObject> personnes = new HashMap<>();

            for (JsonValue p : personneContainer.getJsonArray("personnes")) {

                JsonObject personne = p.asJsonObject();

                personnes.put(personne.getJsonNumber("id").intValue(), personne);
            }


            // 4. Construire la liste des Clients, avec la liste des Personnes pour chaque Client

            JsonArrayBuilder outputClientListe = Json.createArrayBuilder();
            
            for (JsonValue inputClientJsonElement : inputClientListe) {

                JsonObject inputClient = inputClientJsonElement.asJsonObject();
                JsonObjectBuilder outputClient = Json.createObjectBuilder(inputClient); // copie de l'objet Client

                JsonArray personnesID = inputClient.get("personnes-ID").asJsonArray();

                JsonArrayBuilder outputPersonnes = Json.createArrayBuilder();

                for (JsonNumber personneID : personnesID.getValuesAs(JsonNumber.class)) {
                    JsonObject personne = personnes.get(personneID.intValue());
                    outputPersonnes.add(personne);
                }

                outputClient.add("personnes", outputPersonnes);

                outputClientListe.add(outputClient);
            }


            // 5. Ajouter la liste de Clients au conteneur JSON

            this.container.add("clients", outputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("getListeClient", ex);
        }
    }

}

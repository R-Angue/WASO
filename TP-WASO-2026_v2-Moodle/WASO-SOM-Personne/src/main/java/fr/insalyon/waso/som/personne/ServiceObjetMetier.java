package fr.insalyon.waso.som.personne;

import fr.insalyon.waso.util.DBConnection;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.DBException;
import fr.insalyon.waso.util.exception.ServiceException;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.util.List;

/**
 *
 * @author WASO Team
 */
public class ServiceObjetMetier {

    protected DBConnection dBConnection;
    protected JsonObjectBuilder container;

    public ServiceObjetMetier(DBConnection dBConnection, JsonObjectBuilder container) {
        this.dBConnection = dBConnection;
        this.container = container;
    }
    
    public void release() {
        this.dBConnection.close();
    }

    public void getPersonneParId(Integer personneId) throws ServiceException {
        try {
            List<Object[]> personne = this.dBConnection.launchQuery("SELECT PersonneID, Nom, Prenom, Mail FROM PERSONNE WHERE PersonneID = ? ", personneId);

            JsonObjectBuilder jsonItem = Json.createObjectBuilder();

            for (Object[] row : personne) {
                jsonItem.add("id", (Integer) row[0]);
                jsonItem.add("nom", (String) row[1]);
                jsonItem.add("prenom", (String) row[2]);
                jsonItem.add("mail", (String) row[3]);
            }
            this.container.add("personnes", jsonItem);
        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Personne", "getListePersonne", ex);
        }
    }
    
    public void getListePersonne() throws ServiceException {
        try {
            List<Object[]> listePersonne = this.dBConnection.launchQuery("SELECT PersonneID, Nom, Prenom, Mail FROM PERSONNE ORDER BY PersonneID");

            JsonArrayBuilder jsonListe = Json.createArrayBuilder();

            for (Object[] row : listePersonne) {
                JsonObjectBuilder jsonItem = Json.createObjectBuilder();

                jsonItem.add("id", (Integer) row[0]);
                jsonItem.add("nom", (String) row[1]);
                jsonItem.add("prenom", (String) row[2]);
                jsonItem.add("mail", (String) row[3]);

                jsonListe.add(jsonItem);
            }

            this.container.add("personnes", jsonListe);

        } catch (DBException ex) {
            throw JsonServletHelper.ServiceObjectMetierExecutionException("Personne", "getListePersonne", ex);
        }
        
    }
    
    

}

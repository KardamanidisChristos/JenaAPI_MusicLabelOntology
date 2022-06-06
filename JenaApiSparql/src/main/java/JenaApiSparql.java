import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class JenaApiSparql {
    public static void main (String args[]) throws FileNotFoundException {
        final String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX my: <http://www.semanticweb.org/christos/ontologies/2022/4/untitled-ontology-9#>\n";
        Arrays.stream(queries).forEach(s -> executeQuery(prefixes + s));
    }
    private static void executeQuery(String sparqlQuery) {
        try {
            System.out.println(sparqlQuery);
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            String fileName = "src/MusicLabelRDF.owl";
            InputStream inputStream = new FileInputStream(fileName);
            model.read(inputStream, "OWL/XML");
            //model.write(System.out) ;
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
            ResultSetFormatter.out(System.out, results, query);
            qe.close();
        } catch (FileNotFoundException f) {
            f.fillInStackTrace();
        }
    }
    static final String[] queries = {
            "SELECT (COUNT(?Album) as ?almbumMusicStyle)\n" +
                    "    WHERE { ?Album my:albumMusicGenre \"pop\"^^xsd:string}\n",
            "SELECT (MAX(?Song) as ?biggestSong)\n" +
                    "    WHERE { ?Song my:songDuration ?songName}\n",
            "SELECT ?Manager\n" +
                    "\tWHERE { \n" +
                    "\t{?Event my:isPlannedBy ?Manager .\n" +
                    "\t?Event rdf:type my:Festival .\n" +
                    "\t?Event my:capacity 30000}\n" +
                    "\tUNION\n" +
                    "\t{?Event my:isPlannedBy ?Manager .\n" +
                    "\t?Event rdf:type my:Concert .\n" +
                    "\t?Event my:capacity 5000}\n" +
                    "}",
            "SELECT ?songDuration ?releaseDate ?songName\n" +
                    "\tWHERE { ?Song my:songDuration ?songDuration.\n" +
                    "                ?Song my:releaseDate ?releaseDate.\n" +
                    "                ?Song my:songName ?songName.\n" +
                    "              }                                       \n" +
                    "              ORDER BY ?songName \n",
            "SELECT (SUM(?pr * ?cap) AS  ?totalEarnings)\n" +
                    "WHERE{  \n" +
                    "            ?Event my:ticketPrice ?pr.\n" +
                    "            ?Event my:capacity ?cap.\n" +
                    "            ?Event rdf:type my:Festival.       \n" +
                    "            ?Event my:locationOfEvent \"Berlin, Germany\"^^xsd:string. \n" +
                    "            ?Event my:eventDate \"2009-10-01\"^^xsd:dateTime.\n" +
                    "}",
            "SELECT ?studio  ?album \n" +
                    "         WHERE {                         \n" +
                    "                        ?Album my:albumName ?album.\n" +
                    "                        ?Album my:recordedIn ?studio.\n" +
                    "      }                      \n" +
                    "                        GROUP BY ?studio  ?album\n",
            "SELECT ?first ?last\n" +
                    "    WHERE \n" +
                    "    {\n" +
                    "     ?Business_Department my:firstName ?first ; my:lastName ?last; my:birthDate ?birthday.\n" +
                    "                         FILTER( xsd:dateTime(?birthday) >= \"1975-01-01\"^^xsd:dateTime && xsd:dateTime(?birthday) <= \"1985-12-31\"^^xsd:dateTime) \n" +
                    "    }\n",
            "SELECT (?studioName as ?CaliforniaStudios)\n" +
                    "         WHERE {                         \n" +
                    "                        ?Studio my:locationOfStudio ?locStudio; my:studioName ?studioName.\n" +
                    "    FILTER contains (?locStudio,\"California\")\n" +
                    "      }       ",
            "SELECT ?songName\n" +
                    "         WHERE {                         \n" +
                    "                        ?Song my:songName ?songName.\n" +
                    "                        MINUS { ?Song my:releaseDate ?releaseDate.}                     }\n",
            "SELECT ?artisticName ?first ?last ?email\n" +
                    "WHERE\n" +
                    "{  \n" +
                    "      ?Art_Department rdf:type my:Singer.\n" +
                    "      ?Art_Department my:firstName ?first ; my:lastName ?last .\n" +
                    "       OPTIONAL\n" +
                    "             {  ?Art_Department my:knownAs ?artisticName.}\n" +
                    "       OPTIONAL\n" +
                    "             {  ?Art_Department my:email ?email.}\n" +
                    "}\n",
            "SELECT DISTINCT  ?first ?last\n" +
                    "    WHERE { \n" +
                    "    {?Event my:isPlannedBy ?Manager .\n" +
                    "    ?Event rdf:type my:Festival .\n" +
                    "               ?Event my:capacity ?cap.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "    FILTER ( ?cap >= 30000) }\n" +
                    "    UNION\n" +
                    "    {?Event my:isPlannedBy ?Manager .\n" +
                    "    ?Event my:eventDate ?date.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "             FILTER ( ?date > \"2009-01-01\"^^xsd:dateTime) }}\n",
            "SELECT ?first ?last\n" +
                    "    WHERE { \n" +
                    "    {?Event my:isPlannedBy ?Manager .\n" +
                    "    ?Event rdf:type my:Festival .\n" +
                    "               ?Event my:capacity ?cap.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "    FILTER ( ?cap >= 30000) }\n" +
                    "    UNION\n" +
                    "    {?Event my:isPlannedBy ?Manager .\n" +
                    "    ?Event my:eventDate ?date.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "             FILTER ( ?date > \"2009-01-01\"^^xsd:dateTime) }}\n",
            "SELECT (MIN(?salary) as ?minSalary)\n" +
                    "\tWHERE { ?Legal_Department my:salary ?salary}",
            "SELECT (AVG(?salary) as ?avgSalary)\n" +
                    "\tWHERE \n" +
                    "\t{ \t?Art_Department rdf:type my:Manager.\n" +
                    "\t\t?Art_Department my:salary ?salary.\n" +
                    "\t}",
            "SELECT ?first ?last\n" +
                    "\tWHERE \n" +
                    "\t{\n" +
                    "\t ?Musician my:firstName ?first ; my:lastName ?last; my:musicianInstrument ?instrument.\n" +
                    "                         FILTER(?instrument = \"Bassist\" ) \t}",
            "SELECT DISTINCT  ?first ?last\n" +
                    "\tWHERE { \n" +
                    "\t{?Event my:isPlannedBy ?Manager .\n" +
                    "\t?Event rdf:type my:Festival .\n" +
                    "               ?Event my:capacity ?cap.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "\tFILTER ( ?cap >= 30000) }\n" +
                    "\tUNION\n" +
                    "\t{?Event my:isPlannedBy ?Manager .\n" +
                    "\t?Event my:locationOfEvent ?locEvent.\n" +
                    "             ?Manager my:firstName ?first ; my:lastName ?last.\n" +
                    "            FILTER contains (?locEvent,\"Greece\") }}"
    };
}

package br.edu.ifma.dai.processadorsparql.federada.consulta4;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.QJoinDetector;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

/**
 * @author Thiago
 */
public class ConsultaGenericDrugs extends Observable implements Callable<List<String>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaGenericDrugs.class);

    private static final String URL_SERVICE = "http://www4.wiwiss.fu-berlin.de/dailymed/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    public ConsultaGenericDrugs() {
    }

    public ConsultaGenericDrugs(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<String> call() throws Exception {

	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	// lista com o retorno no caso do BindJoin
	List<String> retorno = new ArrayList<String>();
	while (results.hasNext()) {
	    String dgai = results.nextSolution().get("dgai").toString();
	    String dgain = results.nextSolution().get("dgain").toString();
	    String sa = results.nextSolution().get("sa").toString();
	    String gdg = results.nextSolution().get("gdg").toString();

	}
	return retorno;
    }

    /**
     * @return Retorna o objeto query para esta consulta, da classe {@link Query}.
     */
    public Query getQuery() {
	if (query == null) {
	    String queryString = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
		    + " PREFIX owl:      <http://www.w3.org/2002/07/owl#> "
		    + " PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> "
		    + " SELECT ?dgai ?dgain ?sa ?gdg WHERE "
		    + "   		<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:activeIngredient ?dgai . "
		    + "			?dgai rdfs:label ?dgain . "
		    + "			<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:genericDrug ?gdg . "
		    + "			<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> owl:sameAs ?sa . " + "	} ";
	    query = QueryFactory.create(queryString);
	}
	return query;
    }
}
package br.edu.ifma.dai.processadorsparql.federada.consulta4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.QJoinDetector;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * @author Thiago
 */
public class ConsultaPharmacology implements Callable<List<String>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaPharmacology.class);

    private static final String URL_SERVICE = "http://www4.wiwiss.fu-berlin.de/dailymed/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    public ConsultaPharmacology() {
    }

    public ConsultaPharmacology(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<String> call() throws Exception {

	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	List<String> resultado = new ArrayList<String>();

	while (results.hasNext()) {
	    QuerySolution soln = results.nextSolution();
	    String dgin = soln.get("dgin").toString();
	    resultado.add(dgin);
	}
	return resultado;
    }

    public Query getQuery() {
	if (query == null) {

	    String queryString = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
		    + " PREFIX owl:      <http://www.w3.org/2002/07/owl#> "
		    + " PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> "
		    + " SELECT ?dgin WHERE " + "	   		?gdg drugbank:pharmacology ?dgin ; " + "	} ";
	    query = QueryFactory.create(queryString);
	}
	return query;
    }
}
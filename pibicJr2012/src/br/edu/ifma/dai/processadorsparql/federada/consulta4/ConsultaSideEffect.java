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
public class ConsultaSideEffect implements Callable<List<List<String>>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaSideEffect.class);

    private static final String URL_SERVICE = "http://www4.wiwiss.fu-berlin.de/dailymed/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    public ConsultaSideEffect() {
    }

    public ConsultaSideEffect(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<List<String>> call() throws Exception {

	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	List<List<String>> resultado = new ArrayList<List<String>>();
	while (results.hasNext()) {
	    List<String> linha = new ArrayList<String>();

	    QuerySolution soln = results.nextSolution();

	    String se = soln.get("se").toString();
	    String sen = soln.get("sen").toString();

	    linha.add(se);
	    linha.add(sen);

	    resultado.add(linha);
	}

	return resultado;
    }

    public Query getQuery() {
	if (query == null) {

	    String queryString = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
		    + " PREFIX owl:      <http://www.w3.org/2002/07/owl#> "
		    + " PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> "
		    + " SELECT ?se ?sen WHERE " + "			   ?sa sider:sideEffect ?se . "
		    + "	  		   ?se sider:sideEffectName ?sen . " + "	} ";
	    query = QueryFactory.create(queryString);
	}
	return query;
    }
}
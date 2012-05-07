package br.edu.ifma.dai.processadorsparql.federada.consulta2;

import java.util.ArrayList;
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
public class ConsultaActorsNames implements Callable<List<String>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaActorsNames.class);

    private static final String URL_SERVICE = "http://data.linkedmdb.org/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    public ConsultaActorsNames() {
    }

    public ConsultaActorsNames(QJoinDetector qJoinDetector) {
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
	    String actorName = soln.get("actor_name").asLiteral().getString();

	    if (qJoinDetector != null) {
		qJoinDetector.put(actorName, actorName);
	    }

	    resultado.add(actorName);
	}

	return resultado;
    }

    public Query getQuery() {
	if (query == null) {
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("PREFIX movie: <http://data.linkedmdb.org/resource/movie/> ");
	    queryString.append("SELECT ?actor_name ");
	    queryString.append("WHERE { ");
	    queryString.append("<http://data.linkedmdb.org/resource/film/38394> movie:actor ?actor .");
	    queryString.append(" ?actor movie:actor_name ?actor_name . ");
	    queryString.append("}");
	    LOGGER.debug(queryString);
	    query = QueryFactory.create(queryString.toString());
	}
	return query;
    }

}
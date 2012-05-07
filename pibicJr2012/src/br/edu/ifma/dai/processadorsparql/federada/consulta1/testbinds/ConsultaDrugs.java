package br.edu.ifma.dai.processadorsparql.federada.consulta1.testbinds;

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
public class ConsultaDrugs extends Observable implements Callable<List<String>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaDrugs.class);

    private static final String URL_SERVICE = "http://www4.wiwiss.fu-berlin.de/diseasome/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    public ConsultaDrugs() {
    }

    public ConsultaDrugs(QJoinDetector qJoinDetector) {
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
	    String dg = results.nextSolution().get("dg").toString();

	    if (qJoinDetector != null)
		qJoinDetector.put(dg, dg);

	    retorno.add(dg);
	    setChanged();
	    notifyObservers();

	}

	return retorno;
    }

    /**
     * @return Retorna o objeto query para esta consulta, da classe {@link Query}.
     */
    public Query getQuery() {
	if (query == null) {
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("PREFIX diseasome: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/> ");
	    queryString.append("SELECT DISTINCT ?dg ");
	    queryString.append("WHERE {");
	    queryString.append("	?d diseasome:possibleDrug ?dg . ");
	    queryString.append("	?d diseasome:degree ?deg . FILTER (?deg > 20 )");
	    queryString.append(" }");
	    query = QueryFactory.create(queryString.toString());

	}
	return query;
    }

    public static void main(String[] args) {
	try {
	    System.out.println(new ConsultaDrugs().call().size());

	    // StringBuilder queryString = new StringBuilder();
	    // queryString.append("PREFIX diseasome: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/> ");
	    // queryString.append("SELECT DISTINCT ?deg ");
	    // queryString.append("WHERE {");
	    // queryString.append("	?d diseasome:degree ?deg . FILTER (?deg > 20 )");
	    // queryString.append(" }");
	    //
	    // QueryExecution qe =
	    // QueryExecutionFactory.sparqlService(URL_SERVICE,
	    // queryString.toString());
	    // ResultSet results = qe.execSelect();
	    // while (results.hasNext()) {
	    // String c = results.nextSolution().get("categ").toString();
	    // System.out.println(c);
	    // }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
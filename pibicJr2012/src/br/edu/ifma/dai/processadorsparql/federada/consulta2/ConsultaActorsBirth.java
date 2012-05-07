package br.edu.ifma.dai.processadorsparql.federada.consulta2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
public class ConsultaActorsBirth implements Callable<List<List<String>>> {

    private static final String URL_SERVICE = "http://dbpedia.org/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    private List<String> listaActorsName;

    public ConsultaActorsBirth(List<String> listaActorsName) {
	this.listaActorsName = listaActorsName;
    }

    public ConsultaActorsBirth() {
    }

    public ConsultaActorsBirth(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<List<String>> call() throws Exception {

	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	// lista com o retorno no caso do BindJoin
	List<List<String>> retorno = new ArrayList<List<String>>();

	while (results.hasNext()) {
	    QuerySolution soln = results.nextSolution();

	    List<String> linha = new ArrayList<String>();

	    String actorName = soln.get("actor_name").asLiteral().getString();
	    String birthDate = soln.get("birth_date").asLiteral().getString();

	    linha.add(actorName);
	    linha.add(birthDate);

	    if (qJoinDetector != null) {
		qJoinDetector.put(actorName, birthDate);
	    }

	    retorno.add(linha);
	}

	return retorno;
    }

    /**
     * @return Retorna o objeto query para esta consulta, da classe {@link Query}.
     */
    public Query getQuery() {

	if (query == null) {
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ");
	    queryString.append("PREFIX dbpedia: <http://dbpedia.org/ontology/> ");
	    queryString.append("SELECT ?actor_name ?birth_date ");
	    queryString.append("WHERE {");
	    queryString.append("   ?actor2 	a 			dbpedia:Actor .  ");
	    queryString.append("   ?actor2 	foaf:name 		?actor_name .  ");
	    queryString.append("   ?actor2 	dbpedia:birthDate 	?birth_date . ");
	    // se a lista for diferente de null, aplica o filtro (estrategia do
	    // bind).
	    if (listaActorsName != null && listaActorsName.size() > 0) {
		queryString.append("   FILTER(");
		for (String n : listaActorsName)
		    queryString.append(" STR(?actor_name) =  \"" + n + "\" || ");
		// remove a ultima ocorrencia na string
		queryString.delete(queryString.lastIndexOf("||"), queryString.lastIndexOf("||") + 2);
		queryString.append("   )");
	    }
	    queryString.append("} ");
	    query = QueryFactory.create(queryString.toString());

	}
	return query;
    }

    public List<String> getListaActorsName() {
	return listaActorsName;
    }

    public void setListaActorsName(List<String> listaActorsName) {
	this.listaActorsName = listaActorsName;
    }
}
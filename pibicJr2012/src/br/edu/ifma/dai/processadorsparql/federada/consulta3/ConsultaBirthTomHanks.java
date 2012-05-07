package br.edu.ifma.dai.processadorsparql.federada.consulta3;

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
public class ConsultaBirthTomHanks implements Callable<List<List<String>>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaBirthTomHanks.class);

    private static final String URL_SERVICE = "http://dbpedia.org/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    private List<String> listaActorsName;

    public ConsultaBirthTomHanks() {
    }

    public ConsultaBirthTomHanks(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<List<String>> call() throws Exception {
	System.out.println("INI DEBUG\n" + getQuery() + "\nFIM DEBUG");
	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	String var1 = results.getResultVars().get(0);
	// lista com o retorno no caso do BindJoin
	List<List<String>> retorno = new ArrayList<List<String>>();

	while (results.hasNext()) {
	    QuerySolution soln = results.nextSolution();

	    List<String> linha = new ArrayList<String>();

	    String actorNameEN = soln.get("actor_name_en").asLiteral().getString();
	    String birthDate = soln.get("birth_date").asLiteral().getString();

	    linha.add(actorNameEN);
	    linha.add(birthDate);

	    if (qJoinDetector != null) {
		qJoinDetector.put(actorNameEN, birthDate);
	    }

	    // adiciona na lista de retorno.
	    retorno.add(linha);
	}

	LOGGER.debug("fim do processamento");
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
	    queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
	    queryString.append("SELECT ?actor_name_en ?birth_date ");
	    queryString.append("WHERE { ");
	    queryString.append("   ?actor dbpedia:birthDate 	?birth_date . ");
	    queryString.append("   ?actor rdfs:label 		?actor_name_en .  ");
	    queryString.append("   FILTER(lang(?actor_name_en) = \"en\") ");
	    // tive que colocar mais este fiiltro da data pq o serviço da
	    // dbpedia possui alguma limitação
	    // de resultado, não trazendo todos os resultados para uma consulta
	    // grande.
	    queryString.append("   FILTER regex(str(?birth_date), \"1956-07\", \"i\") ");

	    // se a lista for diferente de null, aplica o filtro (estrategia do
	    // bind).
	    if (listaActorsName != null && listaActorsName.size() > 0) {
		queryString.append("   FILTER(");
		for (String n : listaActorsName)
		    queryString.append(" STR(?actor_name_en) =  \"" + n + "\" || ");
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
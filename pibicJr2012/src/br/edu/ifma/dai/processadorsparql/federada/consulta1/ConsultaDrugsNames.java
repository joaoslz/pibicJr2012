package br.edu.ifma.dai.processadorsparql.federada.consulta1;

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
public class ConsultaDrugsNames implements Callable<List<List<String>>> {

    private static final Log LOGGER = LogFactory.getLog(ConsultaDrugsNames.class);

    private static final String URL_SERVICE = "http://www4.wiwiss.fu-berlin.de/dailymed/sparql";

    private QJoinDetector qJoinDetector;

    private Query query = null;

    // Lista de produtores e revisores para adicionar como filtro na consulta de
    // produtos.
    private Collection<String> listaDrugs = null;

    public ConsultaDrugsNames() {
    }

    public ConsultaDrugsNames(Collection<String> listaDrugs) {
	this.listaDrugs = listaDrugs;
    }

    public ConsultaDrugsNames(QJoinDetector qJoinDetector) {
	super();
	this.qJoinDetector = qJoinDetector;
    }

    @Override
    public List<List<String>> call() throws Exception {

	QueryExecution qe = QueryExecutionFactory.sparqlService(URL_SERVICE, getQuery());
	ResultSet results = qe.execSelect();

	// Lista para o resultado no caso de BindJoin. (armazena no formato
	// tabela, com linhas e colunas)
	List<List<String>> resultado = new ArrayList<List<String>>();

	while (results.hasNext()) {
	    // para armazenar os resultados da consulta (cada "registro").
	    List<String> linha = new ArrayList<String>();

	    QuerySolution soln = results.nextSolution();

	    String dg = soln.get("dg").toString();
	    String dgn = soln.get("dgn").toString();

	    // adiciona os valores na lista que representa o "registro" atual
	    // (linha da tabela).
	    linha.add(dg);
	    linha.add(dgn);

	    // adiciona no joinDetector o dg como chave e as outras colunas como
	    // valor.
	    if (qJoinDetector != null) {
		qJoinDetector.put(dg, dgn);
	    }

	    // Adiciona os valores (Linhas da tabela)
	    resultado.add(linha);
	}

	return resultado;
    }

    public Query getQuery() {
	if (query == null) {
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> ");
	    queryString.append("SELECT DISTINCT ?dg ?dgn ");
	    queryString.append("WHERE { ");
	    // caso haja lista de remedios, coloca-os como filtro (BindJoin)
	    if (listaDrugs != null) {
		for (String dg : listaDrugs) {
		    queryString.append("{ <" + dg + "> dailymed:fullName ?dgn } UNION ");
		}
		// remove a ultima ocorrencia na string
		queryString.delete(queryString.lastIndexOf("UNION"), queryString.lastIndexOf("UNION") + 5);
	    }
	    queryString.append("?dg dailymed:fullName ?dgn . ");
	    queryString.append(" }");
	    LOGGER.debug(queryString);
	    query = QueryFactory.create(queryString.toString());
	}
	return query;
    }

    public void setListaDrugs(Collection<String> listaDrugs) {
	this.listaDrugs = listaDrugs;
    }

    public Collection<String> getListaDrugs() {
	return listaDrugs;
    }
}
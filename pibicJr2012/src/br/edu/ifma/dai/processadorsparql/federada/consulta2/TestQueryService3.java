package br.edu.ifma.dai.processadorsparql.federada.consulta2;

import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;

public class TestQueryService3 {

    public static void main(String[] args) {
	System.out.println("Inicio");

	String queryString = "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> "
		+ "PREFIX dbpedia: <http://dbpedia.org/ontology/> " + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
		+ "SELECT  ?actor_name_en ?actor_name ?birth_date " + "WHERE { "
		+ " SERVICE <http://data.linkedmdb.org/sparql> { "
		+ " <http://data.linkedmdb.org/resource/film/38394> movie:actor ?actor . "
		+ " ?actor movie:actor_name ?actor_name ." + "} " + " SERVICE <http://dbpedia.org/sparql> { "
		+ "?actor2 a dbpedia:Actor ; " + "        foaf:name 		?actor_name_en ; "
		+ "        dbpedia:birthDate 	?birth_date . " + " FILTER(STR(?actor_name_en) = ?actor_name) " + " } "
		+ "}";

	// "PREFIX imdb: <http://data.linkedmdb.org/resource/movie/> " +
	// "PREFIX dbpedia: <http://dbpedia.org/ontology/> " +
	// "PREFIX dcterms: <http://purl.org/dc/terms/> " +
	// "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
	// "SELECT * " +
	// "   from <http://xmlns.com/foaf/0.1/> " +
	// "   { " +
	// "       SERVICE <http://data.linkedmdb.org/sparql> " +
	// "       { " +
	// "          ?actor1 imdb:actor_name \"Tom Hanks\". " +
	// "          ?movie imdb:actor ?actor1 ; " +
	// "                 dcterms:title ?movieTitle . " +
	// "       } " +
	// "       SERVICE <http://dbpedia.org/sparql> " +
	// "       { " +
	// "           ?actor rdfs:label \"Tom Hanks\"@en ; " +
	// "                  dbpedia:birthDate ?birth_date . " +
	// "        } " +
	// "   } ";

	System.out.println("query : " + queryString);
	long inicio = System.currentTimeMillis();
	QueryExecution qe = QueryExecutionFactory.create(queryString, Syntax.syntaxARQ, DatasetFactory.create());

	// Executa a consulta e obtem os resultados
	ResultSet results = qe.execSelect();
	System.out.println("Result : " + results.nextBinding());

	// Exibicao dos resultados
	Query query = QueryFactory.create(queryString);
	ResultSetFormatter.out(System.out, results, query);

	// liberar o recurso alocado pela consulta
	qe.close();

	long fim = System.currentTimeMillis();

	System.out.println("Termino em : " + (fim - inicio));
    }
}

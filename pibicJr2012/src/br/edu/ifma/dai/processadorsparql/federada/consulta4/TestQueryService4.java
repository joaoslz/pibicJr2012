package br.edu.ifma.dai.processadorsparql.federada.consulta4;

import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;

public class TestQueryService4 {

    public static void main(String[] args) {
	System.out.println("Inicio");

	String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
		+ "PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> "
		+ "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
		+ "PREFIX sider: <http://www4.wiwiss.fu-berlin.de/sider/resource/sider/> "
		+ "PREFIX dbpprop: <http://dbpedia.org/property/> "
		+ "SELECT ?dgai ?dgain ?sen ?dgin "
		+ "WHERE { "
		+ " 	SERVICE <http://www4.wiwiss.fu-berlin.de/dailymed/sparql> { "
		+ "   		<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:activeIngredient ?dgai . "
		+ "			?dgai rdfs:label ?dgain . "
		+ "			<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:genericDrug ?gdg . "
		+ "			<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> owl:sameAs ?sa . " + "		} "
		+ "		SERVICE <http://www4.wiwiss.fu-berlin.de/drugbank/sparql> { "
		+ "	   		?gdg drugbank:pharmacology ?dgin ; " + "		} "
		// + "	OPTIONAL { "
		+ "		 SERVICE <http://www4.wiwiss.fu-berlin.de/sider/sparql> { " + "			   ?sa sider:sideEffect ?se . "
		+ "	  		   ?se sider:sideEffectName ?sen . " + "		 } "
		// + "	} "
		// + "	OPTIONAL { "
		// + "    	SERVICE <http://dbpedia.org/sparql> { "
		// + "	    	 	?sa dbpprop:legalStatus ?dgls . "
		// + "  	} "
		// + " } "
		+ "} LIMIT 50 ";

	long inicio = System.currentTimeMillis();
	QueryExecution qe = QueryExecutionFactory.create(queryString, Syntax.syntaxARQ, DatasetFactory.create());

	ResultSet results = qe.execSelect();

	Query query = QueryFactory.create(queryString);
	ResultSetFormatter.out(System.out, results, query);

	qe.close();

	long fim = System.currentTimeMillis();

	System.out.println("Termino em : " + (fim - inicio));
    }
}

/*
 * String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
 * "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
 * "PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> " +
 * "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
 * "PREFIX sider: <http://www4.wiwiss.fu-berlin.de/sider/resource/sider/> " +
 * "PREFIX dbpprop: <http://dbpedia.org/property/> " + "SELECT ?dgai ?sen ?dgin ?dgls " + "WHERE { " +
 * " 	SERVICE <http://www4.wiwiss.fu-berlin.de/dailymed/sparql> { " +
 * "   	<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:activeIngredient ?dgai . " +
 * "		?dgai rdfs:label ?dgain . " +
 * "		<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> dailymed:genericDrug ?gdg . " +
 * "		<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/3056> owl:sameAs ?sa . " + "	} " +
 * "	SERVICE <http://www4.wiwiss.fu-berlin.de/drugbank/sparql> { " + "	   ?gdg drugbank:pharmacology ?dgin ; " + "	} " +
 * "	OPTIONAL { " + "		 SERVICE <http://www4.wiwiss.fu-berlin.de/sider/sparql> { " + "			   ?sa sider:sideEffect ?se . "
 * + "	  		   ?se sider:sideEffectName ?sen . " + "		 } " + "	} " + "	OPTIONAL { " +
 * "    		 SERVICE <http://dbpedia.org/sparql> { " + "	    	 ?sa dbpprop:legalStatus ?dgls . " + "  		 } " + " } " +
 * "} ";
 */
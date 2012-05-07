package br.edu.ifma.dai.processadorsparql.federada.consulta1;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;

import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;

public class QueryExecutionDrugsService extends AbstractQueryExecution {

    public static void main(String[] args) {
	try {
	    new QueryExecutionDrugsService().executar();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void executar() throws Exception {
	String queryString =

	"PREFIX diseasome: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/> "
		+ "PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/> "
		+ "SELECT DISTINCT ?dg ?dgn " + "WHERE { "
		+ "	SERVICE <http://www4.wiwiss.fu-berlin.de/diseasome/sparql>  { "
		+ "		<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/3149> diseasome:possibleDrug ?dg . "
		+ " 	} " + "	SERVICE <http://www4.wiwiss.fu-berlin.de/dailymed/sparql> { "
		+ "		?dg dailymed:fullName ?dgn . " + "	} " + "} " + "ORDER BY ?dgn ";

	QueryExecution qe = QueryExecutionFactory.create(queryString, Syntax.syntaxARQ, DatasetFactory.create());
	ResultSet results = qe.execSelect();

	Query query = QueryFactory.create(queryString);
	ResultSetFormatter.out(System.out, results, query);

	qe.close();
    }

    @Override
    public String getName() {
	return "Service-Drug";
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	return 0;
    }
}

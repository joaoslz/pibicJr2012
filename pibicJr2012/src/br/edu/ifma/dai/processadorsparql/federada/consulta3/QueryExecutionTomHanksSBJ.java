package br.edu.ifma.dai.processadorsparql.federada.consulta3;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;

public class QueryExecutionTomHanksSBJ extends AbstractQueryExecution {

    private static final Log LOGGER = LogFactory.getLog(QueryExecutionTomHanksSBJ.class);

    @Override
    public void executar() throws Exception {

	List<String> resultActorsName = executorService.submit(new ConsultaNamesTomHanks()).get();

	ConsultaBirthTomHanks consultaBirthTomHanks = new ConsultaBirthTomHanks();
	consultaBirthTomHanks.setListaActorsName(resultActorsName);

	List<List<String>> resultActorsBirth = executorService.submit(consultaBirthTomHanks).get();

	for (List<String> list : resultActorsBirth) {
	    System.out.println(list);
	}
    }

    @Override
    public String getName() {
	return getClass().getSimpleName();
    }

    public static void main(String[] args) throws Exception {
	long inicio = System.currentTimeMillis();
	new QueryExecutionTomHanksSBJ().executar();
	long fim = System.currentTimeMillis();
	System.out.println("TEMPO: " + (fim - inicio));
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	// TODO Auto-generated method stub
	return 0;
    }
}
package br.edu.ifma.dai.processadorsparql.federada.consulta3;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.QJoinDetector;

public class QueryExecutionTomHanksPHJ extends AbstractQueryExecution {

    private static final Log LOGGER = LogFactory.getLog(QueryExecutionTomHanksPHJ.class);

    @Override
    public void executar() throws Exception {

	QJoinDetector joinDetector = new QJoinDetector();
	joinDetector.setQtdValuesToDetect(2);
	joinDetector.addObserver(new Observer() {
	    @Override
	    public void update(Observable o, Object result) {
		System.out.println(">>" + result);
	    }
	});

	Future<List<String>> fActorsNames = executorService.submit(new ConsultaNamesTomHanks(joinDetector));
	Future<List<List<String>>> fActorsBirth = executorService.submit(new ConsultaBirthTomHanks(joinDetector));

	List<String> names = fActorsNames.get();
    }

    @Override
    public String getName() {
	return getClass().getSimpleName();
    }

    public static void main(String[] args) throws Exception {
	System.out.println("ini: ");
	long inicio = System.currentTimeMillis();
	new QueryExecutionTomHanksPHJ().executar();
	long fim = System.currentTimeMillis();
	System.out.println("TEMPO: " + (fim - inicio));
	// ~6 segundos
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	// TODO Auto-generated method stub
	return 0;
    }
}
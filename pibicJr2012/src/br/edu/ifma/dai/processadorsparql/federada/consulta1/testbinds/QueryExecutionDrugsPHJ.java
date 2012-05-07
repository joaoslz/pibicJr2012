package br.edu.ifma.dai.processadorsparql.federada.consulta1.testbinds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.QJoinDetector;

public class QueryExecutionDrugsPHJ extends AbstractQueryExecution {

    private String name;
    private long inicioExecucao;
    private long tempoPrimeiraDetecao;
    // flag para verificar se é a primeira detecção de join.
    private boolean isPrimeira = true;

    public QueryExecutionDrugsPHJ() {
	super();
    }

    public QueryExecutionDrugsPHJ(String name) {
	super();
	this.name = name;
    }

    @Override
    public void executar() throws Exception {

	final List<Collection<String>> resultadoDeteccoes = new ArrayList<Collection<String>>();
	QJoinDetector joinDetector = new QJoinDetector();
	joinDetector.setQtdValuesToDetect(2);
	joinDetector.addObserver(new Observer() {
	    @Override
	    public void update(Observable o, Object result) {
		// System.out.println(result);
		resultadoDeteccoes.add((Collection<String>) result);
		if (isPrimeira) {
		    long fim = System.currentTimeMillis();
		    tempoPrimeiraDetecao = (fim - inicioExecucao);
		    isPrimeira = false;
		}
	    }
	});
	isPrimeira = true;
	inicioExecucao = System.currentTimeMillis();
	Future<List<String>> fDrugs = executorService.submit(new ConsultaDrugs(joinDetector));
	Future<List<List<String>>> fDrugsNames = executorService.submit(new ConsultaDrugsNames(joinDetector));

	System.out.println("..");
	fDrugs.get();
	fDrugsNames.get();

	System.out.println("Ti: " + tempoPrimeiraDetecao);
	System.out.println("size: " + joinDetector.getQtdDetected());
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	return tempoPrimeiraDetecao;
    }

    public static void main(String[] args) throws Exception {
	long inicio = System.currentTimeMillis();
	new QueryExecutionDrugsPHJ().executar();
	long fim = System.currentTimeMillis();
	System.out.println("TEMPO: " + (fim - inicio));
    }
}
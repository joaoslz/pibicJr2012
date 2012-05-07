package br.edu.ifma.dai.processadorsparql.federada.consulta2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;

public class QueryExecutionActorsSBJ extends AbstractQueryExecution {

    private int qtdRegistrosBind;
    private String name;

    public QueryExecutionActorsSBJ(int qtdRegistrosBind, String name) {
	super();
	this.qtdRegistrosBind = qtdRegistrosBind;
	this.name = name;
    }

    @Override
    public void executar() throws Exception {

	List<String> resultActorsName = executorService.submit(new ConsultaActorsNames()).get();

	// --------------------------------------------------------------------------------------QUEBRA
	// DOS BINDS
	List<Future<List<List<String>>>> tasks = new ArrayList<Future<List<List<String>>>>();
	while (resultActorsName.size() > 0) {
	    List<String> listaBind = new ArrayList<String>();
	    for (int i = 1; i <= qtdRegistrosBind; i++) {
		if (resultActorsName.size() > 0) {
		    String s = resultActorsName.remove(0);
		    listaBind.add(s);
		}
	    }
	    // System.out.println("bind size: " + listaBind.size());
	    ConsultaActorsBirth consultaActorsBirth = new ConsultaActorsBirth(listaBind);
	    Future<List<List<String>>> fActorsBirth = executorService.submit(consultaActorsBirth);
	    tasks.add(fActorsBirth);
	}
	// -------------------------------------------------------------------------------------------------------

	List<List<String>> resultadoUNION = new ArrayList<List<String>>();

	for (Future<List<List<String>>> f : tasks) {
	    resultadoUNION.addAll(f.get());
	}

	System.out.println("size: " + resultadoUNION.size());
	for (List<String> list : resultadoUNION) {
	    // System.out.println(list);
	}
    }

    @Override
    public String getName() {
	return name;
    }

    public static void main(String[] args) throws Exception {
	long inicio = System.currentTimeMillis();
	new QueryExecutionActorsSBJ(8, "").executar();
	long fim = System.currentTimeMillis();
	System.out.println("TEMPO: " + (fim - inicio));
	// ~6 segundos
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	return 0;
    }

    public int getQtdRegistrosBind() {
	return qtdRegistrosBind;
    }

    public void setQtdRegistrosBind(int qtdRegistrosBind) {
	this.qtdRegistrosBind = qtdRegistrosBind;
    }
}
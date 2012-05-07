package br.edu.ifma.dai.processadorsparql.federada.consulta1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;

public class QueryExecutionDrugsSBJ extends AbstractQueryExecution {

    private int qtdRegistrosBind;
    private String name;

    public QueryExecutionDrugsSBJ(int qtdRegistrosBind, String name) {
	super();
	this.qtdRegistrosBind = qtdRegistrosBind;
	this.name = name;
    }

    @Override
    public void executar() throws Exception {

	List<String> resultDrugs = executorService.submit(new ConsultaDrugs()).get();

	// --------------------------------------------------------------------------------------QUEBRA
	// DOS BINDS
	List<Future<List<List<String>>>> tasks = new ArrayList<Future<List<List<String>>>>();
	while (resultDrugs.size() > 0) {
	    List<String> listaBind = new ArrayList<String>();
	    for (int i = 0; i < qtdRegistrosBind; i++) {
		if (resultDrugs.size() > 0) {
		    String s = resultDrugs.remove(0);
		    listaBind.add(s);
		}
	    }
	    ConsultaDrugsNames consultaDrugsNames = new ConsultaDrugsNames(listaBind);
	    Future<List<List<String>>> fProduct = executorService.submit(consultaDrugsNames);
	    tasks.add(fProduct);
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

    @Override
    public long getTempoPrimeiraDeteccao() {
	// TODO Auto-generated method stub
	return 0;
    }

    public int getQtdRegistrosBind() {
	return qtdRegistrosBind;
    }

    public void setQtdRegistrosBind(int qtdRegistrosBind) {
	this.qtdRegistrosBind = qtdRegistrosBind;
    }

    public static void main(String[] args) throws Exception {
	long inicio = System.currentTimeMillis();
	new QueryExecutionDrugsSBJ(1, "").executar();
	long fim = System.currentTimeMillis();
	System.out.println("TEMPO: " + (fim - inicio));
    }
}
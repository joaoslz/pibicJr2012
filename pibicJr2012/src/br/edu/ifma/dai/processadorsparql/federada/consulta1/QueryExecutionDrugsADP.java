package br.edu.ifma.dai.processadorsparql.federada.consulta1;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.QJoinDetector;

public class QueryExecutionDrugsADP extends AbstractQueryExecution {

    private static final Log LOGGER = LogFactory.getLog(QueryExecutionDrugsADP.class);

    private static final long TEMPO_LIMITE = 5000;
    private static final long TAM_MAX_BIND = 500;
    private int qtdBind = 0;
    private String name;

    public QueryExecutionDrugsADP(String name) {
	this.name = name;
    }

    @Override
    public void executar() throws Exception {
	long inicio = System.currentTimeMillis();

	QJoinDetector joinDetector = new QJoinDetector();
	joinDetector.setQtdValuesToDetect(2);
	joinDetector.addObserver(new Observer() {
	    @Override
	    public void update(Observable o, Object result) {
		// System.out.println("HashJoinResult >> " + result.toString());
	    }
	});

	ConsultaDrugsNames consultaDrugsNames = new ConsultaDrugsNames(joinDetector);
	ConsultaDrugs consultaDrugs = new ConsultaDrugs(joinDetector);
	consultaDrugs.addObserver(new Observer() {
	    @Override
	    public void update(Observable o, Object arg) {
		qtdBind++;
	    }
	});

	Future<List<String>> fDrugs = executorService.submit(consultaDrugs);

	while (!executorService.isTerminated()) {
	    // se as consultas ultrapassarem o tempo limite, quebra o laço e
	    // vai para a estratégia HashJoin.
	    if ((System.currentTimeMillis() - inicio) > TEMPO_LIMITE || qtdBind > TAM_MAX_BIND) {
		System.out.println("---------: passou tempo limite (Modou para HashJoin) "
			+ (System.currentTimeMillis() - inicio));
		break;// sai do while.
	    }
	    if (fDrugs.isDone()) {
		// aplica a estratégia de BindJoin
		consultaDrugsNames.setListaDrugs(fDrugs.get());
		break;
	    }
	}

	Future<List<List<String>>> fDrugsNames = executorService.submit(consultaDrugsNames);

	// Se a lista de drugs for diferente de null, é pq aplicou o bind..
	if (consultaDrugsNames.getListaDrugs() != null) {
	    List<List<String>> resultDrugsNames = fDrugsNames.get();
	    for (List<String> list : resultDrugsNames) {
		// System.out.println("BindJoinResult >> " + list);
	    }
	}
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public long getTempoPrimeiraDeteccao() {
	return 0;
    }

}
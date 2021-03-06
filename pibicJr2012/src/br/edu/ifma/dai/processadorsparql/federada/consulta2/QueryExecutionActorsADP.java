package br.edu.ifma.dai.processadorsparql.federada.consulta2;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.QJoinDetector;

public class QueryExecutionActorsADP extends AbstractQueryExecution {

    private static final Log LOGGER = LogFactory.getLog(QueryExecutionActorsADP.class);

    private static final long TEMPO_LIMITE = 5000;

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

	ConsultaActorsNames consultaActorsNames = new ConsultaActorsNames(joinDetector);
	ConsultaActorsBirth consultaActorsBirth = new ConsultaActorsBirth(joinDetector);

	Future<List<String>> fActorsNames = executorService.submit(consultaActorsNames);

	while (!executorService.isTerminated()) {
	    // se as consultas ultrapassarem o tempo limite, quebra o laco e vai
	    // para a estrategia HashJoin.
	    if ((System.currentTimeMillis() - inicio) > TEMPO_LIMITE) {
		System.out.println("-: passou tempo limite (Modou para HashJoin) "
			+ (System.currentTimeMillis() - inicio));
		break;// sai do while.
	    }
	    if (fActorsNames.isDone()) {
		// aplica a estrategia de BindJoin
		consultaActorsBirth.setListaActorsName(fActorsNames.get());
		break;
	    }
	}

	Future<List<List<String>>> fActorsBirth = executorService.submit(consultaActorsBirth);

	// Se a lista de names for diferente de null, eh pq aplicou o bind..
	if (consultaActorsBirth.getListaActorsName() != null) {
	    List<List<String>> resultDrugsNames = fActorsBirth.get();
	    for (List<String> list : resultDrugsNames) {
		// System.out.println("BindJoinResult >> " + list);
	    }
	}
    }

    @Override
    public String getName() {
	return getClass().getSimpleName();
    }

    public static void main(String[] args) throws Exception {
	long inicio = System.currentTimeMillis();
	new QueryExecutionActorsADP().executar();
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
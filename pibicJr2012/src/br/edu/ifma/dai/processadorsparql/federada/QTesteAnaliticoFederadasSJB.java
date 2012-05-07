package br.edu.ifma.dai.processadorsparql.federada;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.SendMail;
import br.edu.ifma.dai.processadorsparql.federada.consulta2.ConsultaActorsNames;
import br.edu.ifma.dai.processadorsparql.federada.consulta2.QueryExecutionActorsSBJ;

public class QTesteAnaliticoFederadasSJB {

    private static final Log LOGGER = LogFactory.getLog(QTesteAnaliticoFederadasSJB.class);

    // obs.: sera realizado 10 execucoes para cada cenario, desconsiderando a
    // primeira e a ultima.
    private static final int QTD_EXECUCOES = 10;

    private Collection<AbstractQueryExecution> listaCenarios;

    /**
     * @param listaCenarios
     */
    public QTesteAnaliticoFederadasSJB(Collection<AbstractQueryExecution> listaCenarios) {
	super();
	this.listaCenarios = listaCenarios;
    }

    /**
     * @throws Exception
     */
    public void testarCenarios() {

	StringBuilder outputGeral = new StringBuilder();

	for (AbstractQueryExecution cenario : listaCenarios) {
	    System.out.println(cenario.getName() + " - INI");

	    // output
	    // geral............................................................................
	    StringBuilder outputFormatted = new StringBuilder();
	    outputFormatted.append(cenario.getName() + "\t");
	    long inicio = -1;
	    long fim = -1;
	    for (int i = 1; i <= QTD_EXECUCOES; i++) {
		boolean ok = false;

		do {
		    try {
			inicio = System.currentTimeMillis();
			cenario.executar();
			fim = System.currentTimeMillis();
			long tempo = fim - inicio;
			// if para desconsiderar o primeiro teste.
			if (i != 1) {
			    outputFormatted.append(tempo + "," + inicio + "\t");
			}
			ok = true;
		    } catch (Exception e) {
			LOGGER.error(e);
			ok = false;
			try {
			    Thread.sleep(120000L);
			} catch (InterruptedException e1) {
			    LOGGER.error(e1);
			}
		    }
		    // while para repetir a execucao caso ocorra erro.
		} while (!ok);
		outputGeral.append(outputFormatted + "\n");
		System.out.println(outputFormatted + "\n");
	    }
	    try {
		SendMail.sendMail("thiagonasper@gmail.com", "RESULTADO DOS TESTES FEDERADOS",
			outputFormatted.toString());
	    } catch (Exception e) {
		LOGGER.error(e);
	    }
	}

	System.out.println(outputGeral + "\n");
	// output
	// email................................................................................
	try {
	    SendMail.sendMail("thiagonasper@gmail.com", "RESULTADO DOS TESTES FEDERADOS", outputGeral.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) throws Exception {
	Collection<AbstractQueryExecution> listaCenarios = new ArrayList<AbstractQueryExecution>();

	// int size = new ConsultaDrugs().call().size();
	// for (int i = 1; i <= size; i++) {
	// listaCenarios.add(new QueryExecutionDrugsSBJ(i, "SBJ-Drugs::" + i ));
	// }

	int size = new ConsultaActorsNames().call().size();
	for (int i = 1; i <= size; i++) {
	    listaCenarios.add(new QueryExecutionActorsSBJ(i, "SBJ-Actors::" + i));
	}

	QTesteAnaliticoFederadasSJB qTesteAnaliticoFederadasSJB = new QTesteAnaliticoFederadasSJB(listaCenarios);
	qTesteAnaliticoFederadasSJB.testarCenarios();
    }
}
package br.edu.ifma.dai.processadorsparql.federada.consulta1.testbinds;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mentawai.mail.EmailException;

import br.edu.ifma.dai.processadorsparql.AbstractQueryExecution;
import br.edu.ifma.dai.processadorsparql.SendMail;
import br.edu.ifma.dai.processadorsparql.federada.consulta2.QueryExecutionActorsPHJ;

public class QTesteAnaliticoFederadasDrugsMultiBind {

    private static final Log LOGGER = LogFactory.getLog(QTesteAnaliticoFederadasDrugsMultiBind.class);

    // obs.: sera realizado 10 execucoes para cada cenario, desconsiderando a
    // primeira e a ultima.
    private static final int QTD_EXECUCOES = 10;

    private Collection<AbstractQueryExecution> listaCenarios;

    /**
     * @param listaCenarios
     */
    public QTesteAnaliticoFederadasDrugsMultiBind(Collection<AbstractQueryExecution> listaCenarios) {
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
	    StringBuilder outputFormatted = new StringBuilder(cenario.getName() + "\n\n");
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
			    outputFormatted.append(tempo + "\n");
			}
			ok = true;
		    } catch (Exception e) {
			LOGGER.error(e);
			ok = false;
			try {
			    Thread.sleep(60000L);
			} catch (InterruptedException e1) {
			    LOGGER.error(e1);
			}
		    }
		    // while para repetir a execucao caso ocorra erro.
		} while (!ok);
	    }
	    outputGeral.append(outputFormatted + "\n");
	    System.out.println(outputFormatted + "\n");
	}

	System.out.println(outputGeral + "\n");
	// output
	// email................................................................................
	try {
	    SendMail.sendMail("joaoslz@gmail.com", "RESULTADO DO Drugs multi bind", outputGeral.toString());
	    SendMail.sendMail("thiagonasper@gmail.com", "RESULTADO DO Drugs multi bind", outputGeral.toString());
	} catch (EmailException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) throws Exception {
	Collection<AbstractQueryExecution> listaCenarios = new ArrayList<AbstractQueryExecution>();

	int sizeBind = new ConsultaDrugs().call().size();
	System.out.println(" BIND: " + sizeBind);
	for (int i = 1; i < sizeBind; i++) {
	    listaCenarios.add(new QueryExecutionDrugsSBJ(sizeBind, "" + i));
	}
	QTesteAnaliticoFederadasDrugsMultiBind qTesteAnaliticoFederadas = new QTesteAnaliticoFederadasDrugsMultiBind(
		listaCenarios);
	qTesteAnaliticoFederadas.testarCenarios();
    }
}
package br.edu.ifma.dai.processadorsparql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * Esta classe representa um cenário de consultas genérico.
 * </p>
 * <p>
 * Cada cenário implementa um estratégia de consulta específica.
 * </p>
 * <p>
 * A idéia é analisar cada cenário de consulta e colher o desempenho obtido em cada um.
 * </p>
 * 
 * @author Thiago
 * 
 */
public abstract class AbstractQueryExecution {

    protected static ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * obs.: atentar para a implementacao de cada cenario, para que nao fique thread perdida na memoria, para que um
     * cenario nao interfira no resultado de outro cen�rio qualquer.
     */
    public abstract void executar() throws Exception;

    public abstract String getName();

    public abstract long getTempoPrimeiraDeteccao();
}

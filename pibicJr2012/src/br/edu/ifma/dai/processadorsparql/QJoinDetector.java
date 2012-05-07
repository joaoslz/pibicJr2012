package br.edu.ifma.dai.processadorsparql;

import java.util.Observable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Thiago
 *         <p>
 *         Classe responsável por receber o resultado de cada consulta (de cada endpoint) e detectar quando ocorrer uma
 *         junção nos resultados.
 *         <p>
 *         Ao detectar uma junção, automaticamente o resultado encontrado é empurrado para cima (push), ou seja, para
 *         os observadores (ou consumidores) que estiverem interessados na informação.
 *         <p>
 *         obs.: a propriedade que for comum entre as duas fontes deve ser considerada como "chave de junção", e esta
 *         chave de junção deve ser desconsiderada por uma das entidades.
 *         <p>
 *         Formula de Junção: qtdAtributos = (qtdAtributosEndPointX + qtdAtributosEndPointY) - 1
 *         </p>
 *         Exemplo de Junção:
 * 
 *         ENDPOINT1 - ENDPOINT2 Producer - Product producer - producer (chave de junção) - product - rating
 * 
 *         obs.: ao adicionar o resultado do endpoint2 eu não adiciono a chave de junção como valor, para que possa
 *         acontecer o join. No mapa: ------------------------------------- p1,p1 - | - p1, product1 | Join Detectado -
 *         p1, rating1 | ------------------------------------- p2,p2 - - p6, product6 - p6, rating6 p3,p3 - - p7,
 *         product7 - p7, rating7 - ------------------------------------- p4,p4 - | - p4, product4 | Join Detectado -
 *         p4, rating4 | -------------------------------------
 * 
 */
public class QJoinDetector extends Observable {

    private Multimap<String, String> multimap = HashMultimap.create();

    private int qtdAtributosRequeridos = 0;

    private int qtdDetected = 0;
    private int qtdDetected2 = 0;

    /**
     * adiciona o resultado de uma consulta no mapa
     * 
     * @param key
     *            atributo que representa a chave primaria
     * @param atributo
     *            outro atributo qualquer resultando da consulta
     */
    public synchronized void put(String key, String atributo) {
	multimap.put(key, atributo);
	int qtdAtributosDetectados = multimap.get(key).size();
	// se a quantidade de atributos detectados for igual a quantidade de
	// atributos requeridos
	// para a consulta, considera-se que ocorreu um join, então os
	// atributos são empurrados para frente (push)
	// e removidos do multimap, deixando apenas a chave de detecção, para
	// o caso de haver outras
	// detecções para a mesma chave.
	if (qtdAtributosDetectados == qtdAtributosRequeridos && multimap.get(key).contains(key)) {
	    setChanged();
	    notifyObservers(multimap.removeAll(key));
	    qtdDetected++;
	    // após remover o registro detectado, é importante adicionar
	    // novamente a chave no multimap para que
	    // possa tornar possível outra detecção para a mesma chave.
	    multimap.put(key, key);
	} else if (qtdAtributosDetectados > qtdAtributosRequeridos) {
	    // se a quantidade de atributos detectados for maior que a de
	    // atributos requeridos, considero que
	    // foi detectado mais de 1 resultado para a mesma chave.
	    qtdDetected2++;
	}
    }

    public synchronized void put2(String key, String atributo) {
	multimap.put(key, atributo);
	int qtdAtributosDetectados = multimap.get(key).size();
	// se a quantidade de atributos detectados for igual a quantidade de
	// atributos requeridos
	// para a consulta, considera-se que ocorreu um join, então os
	// atributos são empurrados para frente (push)
	// e removidos do multimap, deixando apenas a chave de detecção, para
	// o caso de haver outras
	// detecções para a mesma chave.
	if (qtdAtributosDetectados == qtdAtributosRequeridos && multimap.get(key).contains(key)) {
	    setChanged();
	    notifyObservers(multimap.removeAll(key));
	    qtdDetected++;
	    // após remover o registro detectado, é importante adicionar
	    // novamente a chave no multimap para que
	    // possa tornar possível outra detecção para a mesma chave.
	    multimap.put(key, key);
	} else if (qtdAtributosDetectados > qtdAtributosRequeridos) {
	    // se a quantidade de atributos detectados for maior que a de
	    // atributos requeridos, considero que
	    // foi detectado mais de 1 resultado para a mesma chave.
	    qtdDetected2++;
	}
    }

    public void setQtdValuesToDetect(int qtdValuesToDetect) {
	this.qtdAtributosRequeridos = qtdValuesToDetect;
    }

    public int getQtdDetected() {
	return qtdDetected;
    }

    public int getQtdDetected2() {
	return qtdDetected2;
    }
}
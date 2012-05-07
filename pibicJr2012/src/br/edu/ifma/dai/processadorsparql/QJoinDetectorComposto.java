package br.edu.ifma.dai.processadorsparql;

import java.util.Collection;
import java.util.Observable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Thiago
 * 
 */
public class QJoinDetectorComposto extends Observable {

    private Multimap<String, String> multimap1 = HashMultimap.create();
    private Multimap<String, String> multimap2 = HashMultimap.create();
    private Multimap<String, String> multimap3 = HashMultimap.create();

    private int qtdValuesToDetect = 0;

    /**
     * adiciona o resultado de uma consulta no mapa 1
     * 
     * @param s1
     *            atributo que representa a chave primaria
     * @param s2
     *            outro atributo qualquer resultando da consulta
     */
    public synchronized void put1(String key1, String value) {
	multimap1.put(key1, value);

	if (multimap1.get(key1).size() >= qtdValuesToDetect) {
	    Collection<String> values = (Collection<String>) multimap1.get(key1);
	    String key2 = values.iterator().next();
	    // junta a chave do multimap1 com a do multimap2, considerando que a
	    // chave do outro multimap vai sempra ta
	    // na posição 0 (zero) da lista.
	    String compositeKey = key2 + ";" + key1;

	    for (String v : values) {
		multimap3.put(compositeKey, v);
		if (multimap3.get(compositeKey).size() >= qtdValuesToDetect) {
		    setChanged();
		    notifyObservers(multimap3.removeAll(compositeKey));
		}
	    }
	}
    }

    /**
     * adiciona o resultado de uma consulta no mapa 2
     * 
     * @param s1
     *            atributo que representa a chave primaria
     * @param s2
     *            outro atributo qualquer resultando da consulta
     */
    public synchronized void put2(String key2, String value) {
	multimap2.put(key2, value);

	if (multimap2.get(key2).size() >= qtdValuesToDetect) {
	    Collection<String> values = (Collection<String>) multimap2.get(key2);
	    String key1 = values.iterator().next();
	    // junta a chave do multimap2 com a do multimap1, considerando que a
	    // chave do outro multimap vai sempra ta
	    // na posição 0 (zero) da lista.
	    String compositeKey = key2 + ";" + key1;

	    for (String v : values) {
		multimap3.put(compositeKey, v);
		if (multimap3.get(compositeKey).size() >= qtdValuesToDetect) {
		    setChanged();
		    notifyObservers(multimap3.removeAll(compositeKey));
		}
	    }
	}
    }

    public void setQtdValuesToDetect(int qtdValuesToDetect) {
	this.qtdValuesToDetect = qtdValuesToDetect;
    }
}
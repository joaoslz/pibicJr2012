package br.edu.ifma.dai.processadorsparql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * 
 * 
 */
public class QJoinDetectorList extends Observable {

    private Map<String, List<String>> mapa = new HashMap<String, List<String>>();

    private int qtdDetected = 0;

    public synchronized void put(String key, List<String> lista) {
	if (mapa.containsKey(key)) {
	    setChanged();
	    notifyObservers(mapa.remove(key));
	    qtdDetected++;
	} else {
	    mapa.put(key, lista);
	}
    }

    public int getQtdDetected() {
	return qtdDetected;
    }

}
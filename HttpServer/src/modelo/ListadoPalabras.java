package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que genera el ArrayList ListadoPalabras donde se almacenan todas las cadenas y sus
 * métodos para gestionarlo
 * 
 * @author jalfonso
 *
 */
public class ListadoPalabras implements Serializable {

	private static final long serialVersionUID = 6429411508833466096L;

	private List<String> lp = new ArrayList<>();

	/**
	 * Método que añade la cadena que se le pasa como argumento a ListadoPalabras
	 * 
	 * @param s Cadena que se desea añadir a ListadoPalabras
	 */
	public void addString(String s) {
		lp.add(s);
	}

	/**
	 * Método que cuenta el número de veces que existe la palabra que se le pasa
	 * como argumento dentro de ListadoPalabras
	 * 
	 * @param s Palabra a buscar dentro de ListadoPalabras
	 * @return Número de veces que la palabra pasada como argumento se encuentra
	 *         contenida en ListadoPalabras
	 */
	public int searchString(String s) {
		int numCoincidencias = 0;
		for (String p : lp) {
			if (p.toLowerCase().contains(s.toLowerCase())) {
				numCoincidencias++;
			}
		}
		return numCoincidencias;
	}

	/**
	 * Método que elimina todo el contenido de ListadoPalabras
	 */
	public void clear() {
		lp.clear();
	}

	/**
	 * Método que devuelve el contenido de ListadoPalabras
	 * 
	 * @return String Contenido de ListadoPalabras
	 */
	public String list() {
		for (String p : lp) {
			System.out.println("->" + p);
		}
		return lp.toString();
	}
}
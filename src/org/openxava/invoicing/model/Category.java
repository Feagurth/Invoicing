package org.openxava.invoicing.model;

import javax.persistence.*;

/**
 * Clase Categoría
 * 
 * @author Informatica
 *
 */
@Entity
public class Category extends Identifiable {

	/**
	 * Variable para almacenar la descripción de la categoría
	 */
	@Column(length = 50)
	// Definimos el tamaño máximo que tendrá la columna
	private String description;

	/**
	 * Función que nos pemite recuperar la descripción de la categoría
	 * 
	 * @return String La descripción de la categoría
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Función que nos permite asignar la descripción de una categoría
	 * 
	 * @param description
	 *            La descripción de la categoría
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}

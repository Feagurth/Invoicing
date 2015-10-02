package org.openxava.invoicing.model;

import javax.persistence.*;

/**
 * Clase Categor�a
 * 
 * @author Informatica
 *
 */
@Entity
public class Category extends Identifiable {

	/**
	 * Variable para almacenar la descripci�n de la categor�a
	 */
	@Column(length = 50)
	// Definimos el tama�o m�ximo que tendr� la columna
	private String description;

	/**
	 * Funci�n que nos pemite recuperar la descripci�n de la categor�a
	 * 
	 * @return String La descripci�n de la categor�a
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Funci�n que nos permite asignar la descripci�n de una categor�a
	 * 
	 * @param description
	 *            La descripci�n de la categor�a
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}

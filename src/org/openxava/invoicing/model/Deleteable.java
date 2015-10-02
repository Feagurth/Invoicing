package org.openxava.invoicing.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * Superclase para eliminar elementos sin borrarlos de la base de datos,
 * unicametne cambiando la propiedad deleted de los mismos a verdadero o false,
 * según esten borrados o no. Definimos la clase como superclase mapeada para
 * permitir a otras entidades que extienda de ella, sin que esta aparezca como
 * una entidad por sí misma
 * 
 * @author Informatica
 *
 */
@MappedSuperclass
public class Deleteable extends Identifiable {

	/**
	 * Variable para controlar si la factura ha sido borrada
	 */
	@Hidden
	// La variable no se muestra al usuario final
	private boolean deleted;

	/**
	 * Función que nos permite comprobar si la factura ha sido borrada
	 * 
	 * @return boolean True si la factura está borrada, y False si la factura no
	 *         está borrada
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Función que nos permite asignar un estado de borrado a la factura
	 * 
	 * @param deleted
	 *            True si la factura está borrada y False en caso contrario
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}

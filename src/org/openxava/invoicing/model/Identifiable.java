package org.openxava.invoicing.model;

import javax.persistence.*;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * Superclase para asignar un identificador �nico. Definimos la clase como
 * superclase mapeada para permitir a otras entidades que extienda de ella, sin
 * que esta aparezca como una entidad por s� misma
 * 
 * @author Informatica
 *
 */
@MappedSuperclass
public class Identifiable {

	@Id
	// Anotaci�n que especifica la variable como clave primaria de la tabla
	@Hidden
	// Esconde el valor del usuario
	@GeneratedValue(generator = "system-uuid")
	// Genera un n�mero aleatorio de forma autom�tica
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	// Especificamos el sistema de generaci�n de n�meros aleatorios
	@Column(length = 32)
	// Definimos el tama�o m�ximo que tendr� la columna
	private String oid;

	/**
	 * Funci�n que nos permite recupearr el identificador �nico
	 * 
	 * @return String El identificador �nico
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Funci�n que nos permite asignar un identificador
	 * 
	 * @param oid
	 *            El identificador que queremos asignar
	 */
	public void setOid(String oid) {
		this.oid = oid;
	}

}

package org.openxava.invoicing.model;

import javax.persistence.*;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * Superclase para asignar un identificador único. Definimos la clase como
 * superclase mapeada para permitir a otras entidades que extienda de ella, sin
 * que esta aparezca como una entidad por sí misma
 * 
 * @author Informatica
 *
 */
@MappedSuperclass
public class Identifiable {

	@Id
	// Anotación que especifica la variable como clave primaria de la tabla
	@Hidden
	// Esconde el valor del usuario
	@GeneratedValue(generator = "system-uuid")
	// Genera un número aleatorio de forma automática
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	// Especificamos el sistema de generación de números aleatorios
	@Column(length = 32)
	// Definimos el tamaño máximo que tendrá la columna
	private String oid;

	/**
	 * Función que nos permite recupearr el identificador único
	 * 
	 * @return String El identificador único
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Función que nos permite asignar un identificador
	 * 
	 * @param oid
	 *            El identificador que queremos asignar
	 */
	public void setOid(String oid) {
		this.oid = oid;
	}

}

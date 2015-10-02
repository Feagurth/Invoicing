package org.openxava.invoicing.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Clase Clientes
 * 
 * @author Informatica
 *
 */
@Entity
// Definición de la clase Customer como una entidad JPA
@View(name = "Simple", members = "number, name")
// Definimos una vista especial con pocos elementos que usaremos después en la
// visualización de la facturas
public class Customer {

	/**
	 * Variable que almacena el identificador del cliente
	 */
	@Id
	// Propiedad que identifica la variable como la clave del objeto JPA
	@Column(length = 6)
	// Propiedad que define el tamaño máximo que tendrá el campo
	private int number;

	/**
	 * Variable que almacena el nombre del cliente
	 */
	@Column(length = 50)
	// Propiedad que identifica el tamaño máximo que tendrá el campo
	@Required
	// Especifica que el campo es requerido
	private String name;

	@Embedded
	// Referenciamos la clase Address como clase incrustada
	@NoFrame
	// Definimos una etiqueta para que no se muestre marco en la visualización
	// de la dirección
	private Address address;

	/**
	 * Función que nos permite recuperar el identificador del cliente
	 * 
	 * @return int El identificador del cliente
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Función que nos permite asignar el identificador del cliente
	 * 
	 * @param number
	 *            El identificador del cliente que queremos asignar
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Función que nos permite recuperar el nombre del cliente
	 * 
	 * @return String El nombre del cliente
	 */
	public String getName() {
		return name;
	}

	/**
	 * Función que nos permite asignar el nombre de un cliente
	 * 
	 * @param name
	 *            El nombre del cliente que queremos asignar
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Función que nos permite recuperar la dirección asociada al cliente
	 * 
	 * @return Address La dirección asociada al cliente.
	 */
	public Address getAddress() {

		// Verificamos si tenemos algun valor para la dirección
		if (address == null) {
			// Si no es así y la variable es nula, creamos un nuevo objeto
			// address y lo asignamos a la variable para que de este modo nunca
			// sea nula
			address = new Address();
		}

		// Retornamos el valor de la dirección
		return address;
	}

	/**
	 * Función que nos pemite asignar una dirección al cliente
	 * 
	 * @param address
	 *            La dirección que queremos asignar al cliente
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

}

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
// Definici�n de la clase Customer como una entidad JPA
@View(name = "Simple", members = "number, name")
// Definimos una vista especial con pocos elementos que usaremos despu�s en la
// visualizaci�n de la facturas
public class Customer {

	/**
	 * Variable que almacena el identificador del cliente
	 */
	@Id
	// Propiedad que identifica la variable como la clave del objeto JPA
	@Column(length = 6)
	// Propiedad que define el tama�o m�ximo que tendr� el campo
	private int number;

	/**
	 * Variable que almacena el nombre del cliente
	 */
	@Column(length = 50)
	// Propiedad que identifica el tama�o m�ximo que tendr� el campo
	@Required
	// Especifica que el campo es requerido
	private String name;

	@Embedded
	// Referenciamos la clase Address como clase incrustada
	@NoFrame
	// Definimos una etiqueta para que no se muestre marco en la visualizaci�n
	// de la direcci�n
	private Address address;

	/**
	 * Funci�n que nos permite recuperar el identificador del cliente
	 * 
	 * @return int El identificador del cliente
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Funci�n que nos permite asignar el identificador del cliente
	 * 
	 * @param number
	 *            El identificador del cliente que queremos asignar
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Funci�n que nos permite recuperar el nombre del cliente
	 * 
	 * @return String El nombre del cliente
	 */
	public String getName() {
		return name;
	}

	/**
	 * Funci�n que nos permite asignar el nombre de un cliente
	 * 
	 * @param name
	 *            El nombre del cliente que queremos asignar
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Funci�n que nos permite recuperar la direcci�n asociada al cliente
	 * 
	 * @return Address La direcci�n asociada al cliente.
	 */
	public Address getAddress() {

		// Verificamos si tenemos algun valor para la direcci�n
		if (address == null) {
			// Si no es as� y la variable es nula, creamos un nuevo objeto
			// address y lo asignamos a la variable para que de este modo nunca
			// sea nula
			address = new Address();
		}

		// Retornamos el valor de la direcci�n
		return address;
	}

	/**
	 * Funci�n que nos pemite asignar una direcci�n al cliente
	 * 
	 * @param address
	 *            La direcci�n que queremos asignar al cliente
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

}

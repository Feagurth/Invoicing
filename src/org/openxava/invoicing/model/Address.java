package org.openxava.invoicing.model;

import javax.persistence.*;

/**
 * Clase para almacenar la direcci�n
 * 
 * @author Informatica
 *
 */
@Embeddable
// Definimos la clase como clase incrustable
public class Address {

	/**
	 * Variable para almacenar la calle
	 */
	@Column(length = 30)
	// Definimos el tama�o de la columna
	private String street;

	/**
	 * Variable para almacenar el c�digo postal
	 */
	@Column(length = 5)
	// Definimos el tama�o de la columna
	private String zipCode;

	/**
	 * Variable para almacenar la ciudad
	 */
	@Column(length = 20)
	// Definimos el tama�o de la columna
	private String city;

	/**
	 * Variable para almacenar el stado
	 */
	@Column(length = 30)
	// Definimos el tama�o de la columna
	private String state;

	/**
	 * Funci�n que nos permite recuperar la calle asociada a la direcci�n
	 * 
	 * @return String La calle asociada a la direcci�n
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Funci�n que nos permire asignar una calle a una direcci�n
	 * 
	 * @param street
	 *            La calle que queremos asignar a la direcci�n
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Funci�n que nos permite recuperar el c�digo postal asociado a una
	 * direcci�n
	 * 
	 * @return El c�digo postal asociado a la direcci�n
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Funci�n que nos permite asignar un c�digo postal a una direcci�n
	 * 
	 * @param zipCode
	 *            String El c�digo postal que queremos asociado a la direcci�n
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Funci�n que nos permite recuperar la ciudad asociada a la direcci�n
	 * 
	 * @return String La ciudad asociada a la direcci�n
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Funci�n que nos permite asignar una ciudad a la direcci�n
	 * 
	 * @param city
	 *            La ciudad que queremos asignar a la direcci�n
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Funci�n que nos permite recuperar el estado asociado a la direcci�n
	 * 
	 * @return String El estado asociado a la direcci�n
	 */
	public String getState() {
		return state;
	}

	/**
	 * Funci�n que nos permire asignar un estado a la direcci�n
	 * 
	 * @param state
	 *            El estado que queremos asignar a la direcci�n
	 */
	public void setState(String state) {
		this.state = state;
	}

}

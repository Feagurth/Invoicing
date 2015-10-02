package org.openxava.invoicing.model;

import javax.persistence.*;

/**
 * Clase para almacenar la dirección
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
	// Definimos el tamaño de la columna
	private String street;

	/**
	 * Variable para almacenar el código postal
	 */
	@Column(length = 5)
	// Definimos el tamaño de la columna
	private String zipCode;

	/**
	 * Variable para almacenar la ciudad
	 */
	@Column(length = 20)
	// Definimos el tamaño de la columna
	private String city;

	/**
	 * Variable para almacenar el stado
	 */
	@Column(length = 30)
	// Definimos el tamaño de la columna
	private String state;

	/**
	 * Función que nos permite recuperar la calle asociada a la dirección
	 * 
	 * @return String La calle asociada a la dirección
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Función que nos permire asignar una calle a una dirección
	 * 
	 * @param street
	 *            La calle que queremos asignar a la dirección
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Función que nos permite recuperar el código postal asociado a una
	 * dirección
	 * 
	 * @return El código postal asociado a la dirección
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Función que nos permite asignar un código postal a una dirección
	 * 
	 * @param zipCode
	 *            String El código postal que queremos asociado a la dirección
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Función que nos permite recuperar la ciudad asociada a la dirección
	 * 
	 * @return String La ciudad asociada a la dirección
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Función que nos permite asignar una ciudad a la dirección
	 * 
	 * @param city
	 *            La ciudad que queremos asignar a la dirección
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Función que nos permite recuperar el estado asociado a la dirección
	 * 
	 * @return String El estado asociado a la dirección
	 */
	public String getState() {
		return state;
	}

	/**
	 * Función que nos permire asignar un estado a la dirección
	 * 
	 * @param state
	 *            El estado que queremos asignar a la dirección
	 */
	public void setState(String state) {
		this.state = state;
	}

}

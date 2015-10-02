package org.openxava.invoicing.model;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.openxava.annotations.*;

/**
 * Clase para Autor
 * 
 * @author Informatica
 *
 */
@Entity
public class Author extends Identifiable{


	/**
	 * Variable para almacenar el nombre del autor
	 */
	@Column(length = 50)
	// Definimos el tama�o m�ximo que va a tener el campo
	private String name;

	/**
	 * Colecci�n para almacenar los productos relacionados del autor
	 */
	@OneToMany(mappedBy = "author")
	@ListProperties("number, description, price")
	private Collection<Product> products;

	/**
	 * Funci�n que nos permite recuperar el nombre del autor
	 * 
	 * @return String El nombre del autor
	 */
	public String getName() {
		return name;
	}

	/**
	 * Funci�n que nos permite asignar un nombre al autor
	 * 
	 * @param name
	 *            El nombre que queremos asignar al autor
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Funci�n que nos permite recuperar la colecci�n de productos relacionados
	 * con el autor
	 * 
	 * @return Collection<Product> La colecci�n de productos relacionados con el
	 *         autor
	 */
	public Collection<Product> getProducts() {
		return products;
	}

	/**
	 * Funci�n para asignar una colecci�n de productos relacionado con un autor
	 * 
	 * @param products
	 */
	public void setProducts(Collection<Product> products) {
		this.products = products;
	}
}

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
	// Definimos el tamaño máximo que va a tener el campo
	private String name;

	/**
	 * Colección para almacenar los productos relacionados del autor
	 */
	@OneToMany(mappedBy = "author")
	@ListProperties("number, description, price")
	private Collection<Product> products;

	/**
	 * Función que nos permite recuperar el nombre del autor
	 * 
	 * @return String El nombre del autor
	 */
	public String getName() {
		return name;
	}

	/**
	 * Función que nos permite asignar un nombre al autor
	 * 
	 * @param name
	 *            El nombre que queremos asignar al autor
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Función que nos permite recuperar la colección de productos relacionados
	 * con el autor
	 * 
	 * @return Collection<Product> La colección de productos relacionados con el
	 *         autor
	 */
	public Collection<Product> getProducts() {
		return products;
	}

	/**
	 * Función para asignar una colección de productos relacionado con un autor
	 * 
	 * @param products
	 */
	public void setProducts(Collection<Product> products) {
		this.products = products;
	}
}

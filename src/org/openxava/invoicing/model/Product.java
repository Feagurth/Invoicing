package org.openxava.invoicing.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.invoicing.annotations.*;

/**
 * Clase Producto
 * 
 * @author Informatica
 *
 */
@Entity
@View(name = "Simple", members = "number, description")
// Definimos una vista con nombre Simple con solo el número y la descripción
public class Product {

	/**
	 * Variable que almacena el identificador del producto
	 */

	@Id
	// Propiedad que identifica la variable como la clave del objeto JPA
	@Column(length = 9)
	// Propiedad que define el tamaño máximo que tendrá el campo
	private int number;

	/**
	 * Variable que almacena la descripción del producto
	 */
	@Column(length = 50)
	// Propiedad que define el tamaño máximo que tendrá el campo
	@Required
	// Especifica que el campo es requerido
	private String description;

	/**
	 * Variable para almacenar la información del autor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	// Definimos la relación de autores y productos
	@DescriptionsList
	// Mostramos la información sobre autores en un combo desplegable
	private Author author;

	/**
	 * Variable para almacenar la categoría asociada al producto
	 */
	@ManyToOne( // Especificamos como se va a almacenar la referencia al objeto
				// Category en la base de datos, en este caso como una relación
				// de muchos a 1
	fetch = FetchType.LAZY, // Especificamos que la carga de datos se realizará
							// bajo demanda.
	optional = true)
	// Especificamos que la referencia puede ser nula
	@DescriptionsList
	// Definimos que la referencia va a ser vista como un desplegable
	private Category category;

	/**
	 * Variable para almacenar el precio del producto
	 */
	@Stereotype("MONEY")
	// Especificamos que la variable se comporte como un estereotipo de tipo
	// dinero
	private BigDecimal price;

	/**
	 * Array que nos permte almacenar la foto del producto
	 */
	@Stereotype("PHOTO")
	// Especificamos que la variable se comporte como un estereotipo tipo foto
	private byte[] photo;

	/**
	 * Variable para almacenar la clave de la galería de fotos
	 */
	@Stereotype("IMAGES_GALLERY")
	// Especificamos que la variable se comporte como un estereotipo de galería
	// de imágenes
	@Column(length = 32)
	// Definimos el tamaño máximo que tendrá el campo para almacenar la clave de
	// la galería.
	private String morePhotos;

	/**
	 * Variable para almacenar comentarios sobre el producto.
	 */
	@Stereotype("NEMO")
	// Especificamos que la variable se comporte como un estereotipo de tipo
	// memo
	private String remarks;

	/**
	 * Variable para almacenar isbn
	 */
	@Column(length = 10)
	// Definimos el tamaño máximo del campo en la base de datos
	@ISBN(search=true) // Usamos la anotación @isbn que hemos creado para validar el número ISBN de los productos
	private String isbn;

	/**
	 * Función que nos permite recuperar el identificador del número
	 * 
	 * @return int El identificador del producto
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Función que nos permite establecer el identificador del producto
	 * 
	 * @param number
	 *            El indentificador que queremos asignar al producto
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Función que nos permite recuperar la descripción del producto
	 * 
	 * @return String Devuelve la descripción del producto
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Función que nos permite asignar la descripción del producto
	 * 
	 * @param description
	 *            La descripción que queremos asignar al producto
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Función que nos permite recuperar la información del autor relacionado
	 * con el producto
	 * 
	 * @return Author El autor relacionado con el producto
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * Función que nos permite asignar un autor al producto
	 * 
	 * @param author
	 *            El autor que queremos asignar al producto
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * Función que nos permite recuperar la categoría asociada al producto
	 * 
	 * @return La categoría a la que pertenece el producto
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Función que nos permite asignar una categoría al producto
	 * 
	 * @param category
	 *            Category La categoría que queremos asignar al producto
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * Función que permite recuperar el precio del producto.
	 * 
	 * @return BigDecimal El precio del producto
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Función que nos permite asignar el precio a un producto.
	 * 
	 * @param price
	 *            El precio que queremos asignar a un producto.
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * Función que nos permite recuperar la imagen asociada al producto
	 * 
	 * @return byte[] La imagen asociada al producto
	 */
	public byte[] getPhoto() {
		return photo;
	}

	/**
	 * Función que nos permite asignar una imagen al producto
	 * 
	 * @param photo
	 *            La imagen que queremos asignar al producto
	 */
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	/**
	 * Función que nos permite recuperar el identificador de la galería de
	 * imágenes
	 * 
	 * @return String El identificador de la galería de imágenes.
	 */
	public String getMorePhotos() {
		return morePhotos;
	}

	/**
	 * Función que nos permite asignar el identificador de una galería de
	 * imágenes al producto
	 * 
	 * @param morePhotos
	 *            El identificador de la galería de imágenes.
	 */
	public void setMorePhotos(String morePhotos) {
		this.morePhotos = morePhotos;
	}

	/**
	 * Función que nos permite recuperar las notas relativas al producto
	 * 
	 * @return String Las notas relativas al producto
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Función que nos permite asignar las notas relativas al producto
	 * 
	 * @param remarks
	 *            Las notas relativas al producto
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Función que nos permite recuperar el isbn de un producto
	 * 
	 * @return String El isbn del producto
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * Función que nos permite asignar un isbn a un producto
	 * 
	 * @param isbn
	 *            El isbn que queremos asignar
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}

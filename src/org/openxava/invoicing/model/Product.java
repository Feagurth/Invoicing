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
// Definimos una vista con nombre Simple con solo el n�mero y la descripci�n
public class Product {

	/**
	 * Variable que almacena el identificador del producto
	 */

	@Id
	// Propiedad que identifica la variable como la clave del objeto JPA
	@Column(length = 9)
	// Propiedad que define el tama�o m�ximo que tendr� el campo
	private int number;

	/**
	 * Variable que almacena la descripci�n del producto
	 */
	@Column(length = 50)
	// Propiedad que define el tama�o m�ximo que tendr� el campo
	@Required
	// Especifica que el campo es requerido
	private String description;

	/**
	 * Variable para almacenar la informaci�n del autor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	// Definimos la relaci�n de autores y productos
	@DescriptionsList
	// Mostramos la informaci�n sobre autores en un combo desplegable
	private Author author;

	/**
	 * Variable para almacenar la categor�a asociada al producto
	 */
	@ManyToOne( // Especificamos como se va a almacenar la referencia al objeto
				// Category en la base de datos, en este caso como una relaci�n
				// de muchos a 1
	fetch = FetchType.LAZY, // Especificamos que la carga de datos se realizar�
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
	 * Variable para almacenar la clave de la galer�a de fotos
	 */
	@Stereotype("IMAGES_GALLERY")
	// Especificamos que la variable se comporte como un estereotipo de galer�a
	// de im�genes
	@Column(length = 32)
	// Definimos el tama�o m�ximo que tendr� el campo para almacenar la clave de
	// la galer�a.
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
	// Definimos el tama�o m�ximo del campo en la base de datos
	@ISBN(search=true) // Usamos la anotaci�n @isbn que hemos creado para validar el n�mero ISBN de los productos
	private String isbn;

	/**
	 * Funci�n que nos permite recuperar el identificador del n�mero
	 * 
	 * @return int El identificador del producto
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Funci�n que nos permite establecer el identificador del producto
	 * 
	 * @param number
	 *            El indentificador que queremos asignar al producto
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Funci�n que nos permite recuperar la descripci�n del producto
	 * 
	 * @return String Devuelve la descripci�n del producto
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Funci�n que nos permite asignar la descripci�n del producto
	 * 
	 * @param description
	 *            La descripci�n que queremos asignar al producto
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Funci�n que nos permite recuperar la informaci�n del autor relacionado
	 * con el producto
	 * 
	 * @return Author El autor relacionado con el producto
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * Funci�n que nos permite asignar un autor al producto
	 * 
	 * @param author
	 *            El autor que queremos asignar al producto
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * Funci�n que nos permite recuperar la categor�a asociada al producto
	 * 
	 * @return La categor�a a la que pertenece el producto
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Funci�n que nos permite asignar una categor�a al producto
	 * 
	 * @param category
	 *            Category La categor�a que queremos asignar al producto
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * Funci�n que permite recuperar el precio del producto.
	 * 
	 * @return BigDecimal El precio del producto
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Funci�n que nos permite asignar el precio a un producto.
	 * 
	 * @param price
	 *            El precio que queremos asignar a un producto.
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * Funci�n que nos permite recuperar la imagen asociada al producto
	 * 
	 * @return byte[] La imagen asociada al producto
	 */
	public byte[] getPhoto() {
		return photo;
	}

	/**
	 * Funci�n que nos permite asignar una imagen al producto
	 * 
	 * @param photo
	 *            La imagen que queremos asignar al producto
	 */
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	/**
	 * Funci�n que nos permite recuperar el identificador de la galer�a de
	 * im�genes
	 * 
	 * @return String El identificador de la galer�a de im�genes.
	 */
	public String getMorePhotos() {
		return morePhotos;
	}

	/**
	 * Funci�n que nos permite asignar el identificador de una galer�a de
	 * im�genes al producto
	 * 
	 * @param morePhotos
	 *            El identificador de la galer�a de im�genes.
	 */
	public void setMorePhotos(String morePhotos) {
		this.morePhotos = morePhotos;
	}

	/**
	 * Funci�n que nos permite recuperar las notas relativas al producto
	 * 
	 * @return String Las notas relativas al producto
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Funci�n que nos permite asignar las notas relativas al producto
	 * 
	 * @param remarks
	 *            Las notas relativas al producto
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Funci�n que nos permite recuperar el isbn de un producto
	 * 
	 * @return String El isbn del producto
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * Funci�n que nos permite asignar un isbn a un producto
	 * 
	 * @param isbn
	 *            El isbn que queremos asignar
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}

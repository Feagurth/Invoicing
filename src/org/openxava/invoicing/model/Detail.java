package org.openxava.invoicing.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.invoicing.calculators.*;

@Entity
@View(members = "product; quantity, pricePerUnit, amount")
// Definimos la vista que va a tener el formulario de detalles
public class Detail extends Identifiable {

	/**
	 * Definimos un objeto factura que ser� el padre de la linea actual
	 */
	@ManyToOne
	// Realizamos la relacci�n sin especificar nada, para que no haya errores.
	private CommercialDocument parent;

	/**
	 * Variable para almacenar la cantidad de unidades de producto que tiene la
	 * linea
	 */
	@Required
	// Obligamos a que la cantidad sea introducida por el usuario
	private int quantity;

	/**
	 * Variable para almacenar el objeto Producto correspondiente a al linea
	 */
	@ManyToOne( // Definimos la relaci�n de la linea con los productos
	fetch = FetchType.LAZY, // Especificamos que los datos se carguen solamente
							// cuando sea necesario
	optional = false)
	// Definimos el producto como no opcional para la linea. Una linea de
	// producto siempre debe tener un producto como m�nimo.
	@ReferenceView("Simple")
	// Usamos la vista simple definida en producto para visualizar la
	// informaci�n
	@NoFrame
	// Usamos la vista sin marco alrededor
	private Product product;

	// Definimos lc lase PricePerUnitCalculator como el calculador por defecto
	// de pricePerUnit.
	@DefaultValueCalculator(value = PricePerUnitCalculator.class, properties = @PropertyValue(name = "productNumber", from = "product.number"))
	@Stereotype("MONEY")
	// Definimos que se trate el resultado como tipo dinero
	private BigDecimal pricePerUnit;

	/**
	 * Funci�n que nos permite conocer el montante de un producto multiplicando
	 * su precio por la cantidad de ellos seleccionados
	 * 
	 * @return BigDecimal El precio total de la linea de detalle
	 */
	@Stereotype("MONEY")
	// Especificamos que los resultado sean de tipo dinero
	@Depends("pricePerUnit, quantity")
	// Y definimos la dependencia de la funci�n de los valores de precio por
	// unidad y cantidad
	public BigDecimal getAmount() {
		// Multiplicamos la cantidad por el precio por unidad y devolvemos el
		// resultado
		return new BigDecimal(quantity).multiply(getPricePerUnit());
	}

	/**
	 * Funci�n que nos permite recuperar la factura a la cual pertenece la linea
	 * de detalle
	 * 
	 * @return Invoice La factura a la cual pertenece la linea de detalle
	 */
	public CommercialDocument getParent() {
		return parent;
	}

	/**
	 * Funci�n que nos permite asignar una factura a la linea de detalle actual
	 * 
	 * @param parent
	 *            La factura a la cual pertenece la linea de detalle actual.
	 */
	public void setParent(CommercialDocument parent) {
		this.parent = parent;
	}

	/**
	 * Funci�n que nos permite recuperar la cantidad de art�culos que tiene la
	 * linea de detalle
	 * 
	 * @return int La cantidad de art�culos que tiene la linea de detalle
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Funci�n que nos permite asignar una cantidad de art�culos a la linea de
	 * detalle
	 * 
	 * @param quantity
	 *            La cantidad de art�culos que tiene la linea de detalle
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Funci�n que nos permite recuperar el producto que contiene la linea de
	 * detalle
	 * 
	 * @return El producto que contiene la linea de detalle
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * Funci�n que nos permite asignar un producto a la linea de detalle.
	 * 
	 * @param product
	 *            El producto que deseamos asignar a la linea de detalle
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * Funci�n que nos permite recuperar el precio por unidad de una linea de
	 * detalle
	 * 
	 * @return BigDecimal El precio por unidad de la linea de detalle
	 */
	public BigDecimal getPricePerUnit() {
		// Devolvemos 0 si no tenemos un valor, en caso contrario devolvemos el
		// resultado
		return pricePerUnit == null ? BigDecimal.ZERO : pricePerUnit;
	}

	/**
	 * Funci�n que nos permite asignar un precio por unidad a una linea de
	 * detalle
	 * 
	 * @param pricePerUnit
	 *            El precio por unidad que queremos asignar a la linea de
	 *            detalle
	 */
	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	/**
	 * Funci�n que nos permite recalcular el importe de un documento comercial
	 * cuando se a�ade una nueva linea de detalle
	 */
	@PrePersist
	// Especificamos que se realice la funci�n antes de grabar por primera vez
	// los datos
	private void onPersist() {
		// Para mantener la colecci�n sincronizada
		getParent().getDetails().add(this);

		// Realizamos el c�lculo
		getParent().recalculateAmount();

	}

	/**
	 * Funci�n que nos permite recalcular el importe de un documento comercial
	 * cuando se modifique una linea de detalle
	 */
	@PreUpdate
	// Especificamos que la funci�n se realice cada vez que el detalle se
	// modifica
	private void onUpdate() {
		// Realizamos el c�lculo
		getParent().recalculateAmount();
	}

	/**
	 * Funci�n que nos permite recalcular el importe de un documento comercial
	 * cuando se elimine una linea de detalle
	 */
	@PreRemove
	// Especificamos que se realice la funci�n antes de eliminar los datos
	private void onRemove() {
		// Verificamos si el documento comercial del que forma parte la linea est� siendo eliminado
		if (getParent().isRemoving()) {
			
			// Si es as�, hacemos return para evitar errores
			return;
		} else {
			// En caso contrario
			// Para mantener la colecci�n sincronizada
			getParent().getDetails().remove(this);

			// Realizamos el c�lculo
			getParent().recalculateAmount();
		}
	}
}

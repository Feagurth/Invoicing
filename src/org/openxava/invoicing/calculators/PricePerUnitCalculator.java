package org.openxava.invoicing.calculators;

import org.openxava.calculators.*;
import org.openxava.invoicing.model.*;

// Importe para usar getManager()
import static org.openxava.jpa.XPersistence.*;

public class PricePerUnitCalculator implements ICalculator {

	/**
	 * Uid generado
	 */
	private static final long serialVersionUID = -5994245957256825082L;

	// Variable para almacenar el número de producto
	private int productNumber;

	public Object calculate() throws Exception {

		// Buscamos el producto usando el número de producto para localizarlo
		Product product = getManager().find(Product.class, productNumber);

		// Recuperamos el precio del producto y lo devolvemos
		return product.getPrice();
	}

	/**
	 * Función que nos permite recuperar el número de producto
	 * 
	 * @return El número del producto
	 */
	public int getProductNumber() {
		return productNumber;
	}

	/**
	 * Función que nos permite asignar un número de producto
	 * 
	 * @param productNumber
	 *            El número del producto que queremos asignar
	 */
	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}

}
package org.openxava.invoicing.actions;

import org.openxava.actions.*;

/**
 * Clase para realizar la acción personalizada de buscar una factura desde un
 * pedido Extiende de ReferenceSearchAction para usar la lógica estándar para
 * buscar una referencia
 * 
 * @author Informatica
 *
 */
public class SearchInvoiceFromOrderAction extends ReferenceSearchAction {

	/**
	 * Función que se va a ejecutar cuando se realice la acción
	 */
	public void execute() throws Exception {
		// Ejecutamos la lógica estándard, por la cual se muestra un dialogo
		super.execute();

		// Recogemos de la vista anterior (la que no es un dialogo) el número de
		// cliente y lo almacenamos en una variable
		int customerNumber = getPreviousView().getValueInt("customer.number");

		// Verificamos si hemos conseguido leer un número, lo que implicará que
		// hay un cliente
		if (customerNumber > 0) {
			// Si tenemos un número de cliente, lo usaremos para filtrar los
			// datos existentes
			getTab().setBaseCondition("${customer.number} = " + customerNumber);
		}

	}

}

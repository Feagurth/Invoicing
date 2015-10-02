package org.openxava.invoicing.actions;

import org.openxava.actions.*;

/**
 * Clase para realizar la acci�n personalizada de buscar una factura desde un
 * pedido Extiende de ReferenceSearchAction para usar la l�gica est�ndar para
 * buscar una referencia
 * 
 * @author Informatica
 *
 */
public class SearchInvoiceFromOrderAction extends ReferenceSearchAction {

	/**
	 * Funci�n que se va a ejecutar cuando se realice la acci�n
	 */
	public void execute() throws Exception {
		// Ejecutamos la l�gica est�ndard, por la cual se muestra un dialogo
		super.execute();

		// Recogemos de la vista anterior (la que no es un dialogo) el n�mero de
		// cliente y lo almacenamos en una variable
		int customerNumber = getPreviousView().getValueInt("customer.number");

		// Verificamos si hemos conseguido leer un n�mero, lo que implicar� que
		// hay un cliente
		if (customerNumber > 0) {
			// Si tenemos un n�mero de cliente, lo usaremos para filtrar los
			// datos existentes
			getTab().setBaseCondition("${customer.number} = " + customerNumber);
		}

	}

}

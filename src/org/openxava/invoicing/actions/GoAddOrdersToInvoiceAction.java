package org.openxava.invoicing.actions;

import org.openxava.actions.*;

/**
 * Clase que nos permite a�adir pedidos desde una factura. Extendemos de
 * GoAddElementsToCollectionAction para poder usar la l�gica est�ndar para ir a
 * la lista que permite a�adir elementos a la colecci�n
 * 
 * @author Informatica
 *
 */
public class GoAddOrdersToInvoiceAction extends GoAddElementsToCollectionAction {

	public void execute() throws Exception {

		// Ejecutamos la l�gica est�ndar de GoAddElementsToCollectionAction que
		// muestra una ventana de dialogo
		super.execute();

		// Recuperamos la vista anterior a la ventana de dialogo, donde
		// recuperamos el n�mero del cliente
		int customerNumber = getPreviousView().getValueInt("customer.number");

		// Asignamos como condici�n de la lista de pedidos que los registros que
		// se muestren hayan sido enviados, el n�mero de cliente del pedido y la
		// factura sea el mismo y que la factura sea nula
		getTab().setBaseCondition(
				"${customer.number} = " + customerNumber
						+ " and ${delivered} = true and ${invoice.oid} is null");
	}

	/**
	 * Funci�n que nos permite especificar cual ser� la siguiente acci�n a
	 * realizar
	 */
	public String getNextController() {
		return "AddOrdersToInvoice";
	}

}

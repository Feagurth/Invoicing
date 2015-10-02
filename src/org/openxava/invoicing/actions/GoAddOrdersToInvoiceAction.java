package org.openxava.invoicing.actions;

import org.openxava.actions.*;

/**
 * Clase que nos permite añadir pedidos desde una factura. Extendemos de
 * GoAddElementsToCollectionAction para poder usar la lógica estándar para ir a
 * la lista que permite añadir elementos a la colección
 * 
 * @author Informatica
 *
 */
public class GoAddOrdersToInvoiceAction extends GoAddElementsToCollectionAction {

	public void execute() throws Exception {

		// Ejecutamos la lógica estándar de GoAddElementsToCollectionAction que
		// muestra una ventana de dialogo
		super.execute();

		// Recuperamos la vista anterior a la ventana de dialogo, donde
		// recuperamos el número del cliente
		int customerNumber = getPreviousView().getValueInt("customer.number");

		// Asignamos como condición de la lista de pedidos que los registros que
		// se muestren hayan sido enviados, el número de cliente del pedido y la
		// factura sea el mismo y que la factura sea nula
		getTab().setBaseCondition(
				"${customer.number} = " + customerNumber
						+ " and ${delivered} = true and ${invoice.oid} is null");
	}

	/**
	 * Función que nos permite especificar cual será la siguiente acción a
	 * realizar
	 */
	public String getNextController() {
		return "AddOrdersToInvoice";
	}

}

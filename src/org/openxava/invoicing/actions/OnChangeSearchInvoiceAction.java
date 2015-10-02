package org.openxava.invoicing.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.invoicing.model.*;
import org.openxava.model.*;
import org.openxava.view.*;

/**
 * Clase que se va a usar para realizar busquedas de facturas cuando se pierda
 * el foco. Extendemos de OnChangeSearchAction para poder usar la lógica
 * estándard para buscar una referencia cuando los valores clave cambien en la
 * interfaz de usuario
 * 
 * @author Informatica
 *
 */
public class OnChangeSearchInvoiceAction extends OnChangeSearchAction {

	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {

		// Ejecutamos la lógica estándard de OnChangeSearchAction
		super.execute();

		// Recuperamos los valores de la vista actual
		Map keyValues = getView().getKeyValuesWithValue();

		// Verificamos si tenemos algún valor almacenado tras la recuperación
		if (keyValues.isEmpty()) {
			// Si no tenemos valor alguno, no tenemos nada que hacer aquí.
			return;
		}

		// Recuperamos la factura asociada buscando la clave asociada que se
		// haya tecleado
		Invoice invoice = (Invoice) MapFacade.findEntity(getView()
				.getModelName(), keyValues);

		// Accedemos a la visa del cliente y la almacenamos
		View customerView = getView().getRoot().getSubview("customer");

		// A partir de la vista del cliente sacamos el número de cliente
		int customerNumber = customerView.getValueInt("number");

		// Comprobamos si tenemos un número de cliente, y por tanto un cliente
		// seleccionado de antemano
		if (customerNumber == 0) {
			// Si no tenemos cliente, rellenamos el campo número con el número
			// de cliente de la factura
			customerView.setValue("number", invoice.getCustomer().getNumber());

			// Y refrescamos la vista
			customerView.refresh();
		} else {
			// Si tenemos cliente, comprobamos que su número sea el mismo que el
			// del cliente de la factura asociada
			if (customerNumber != invoice.getCustomer().getNumber()) {
				// Si no es el caso, mostramos un error y refrescamos la vista
				addError("invoice_customer_not_match", invoice.getCustomer()
						.getNumber(), invoice, customerNumber);
				getView().refresh();
			}
		}

	}

}

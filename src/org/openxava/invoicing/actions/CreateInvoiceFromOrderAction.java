package org.openxava.invoicing.actions;

import org.openxava.actions.*;
import org.openxava.invoicing.model.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

/**
 * Clase para generar una factura a partir de un pedido Extiende de
 * ViewBaseAction para poder hacer uso de getView() Implemetnamos de
 * IHideActionAction para poder ocultar el botón que lanza la acción una vez se
 * haya realizado
 * 
 * @author Informatica
 *
 */
public class CreateInvoiceFromOrderAction extends ViewBaseAction implements
		IHideActionAction {

	private boolean hideAction = false;

	/**
	 * Función que se ejecuta para realizar la lógica de la clase
	 */
	public void execute() throws Exception {

		// Intentamos recuperar el id de pedido actual
		Object oid = getView().getValue("oid");

		// Comprobamos si el pedido actual tiene un oid asignado
		if (oid == null) {
			// Si no lo tiene, el pedido no ha sido grabado y por tanto no se
			// puede generar una factura de un pedido que no ha sido grabado con
			// anterioridad, por lo que generaremos un error
			addError("impossible_create_invoice_order_not_exists");
			return;
		}

		// Si el pedido existe, lo grabamos en la base de datos haciendo uso de
		// MapFacade, para tener la información del pedido sincronizada y así
		// evitar tener problemas de congruencia respecto a los datos mostrados
		// en pantalla al realizar la factura
		MapFacade.setValues("Order", getView().getKeyValues(), getView()
				.getValues());

		// Recuperamos el pedido en el que nos encontramos
		Order order = XPersistence.getManager().find(Order.class,
				getView().getValue("oid"));

		// Creamos la factura delegando el trabajo en el objeto order
		order.createInvoice();

		// Refrescamos la vista para actualizar los cambios
		getView().refresh();

		// Añadimos un mensaje de información para el usuario
		addMessage("invoice_created_from_order", order.getInvoice());

		// Si todo ha salido bien, ocultamos el botón que lanza la acción
		hideAction = true;
	}

	/**
	 * Función que nos permite recuperar la acción a ocultar
	 */
	public String getActionToHide() {
		// Comprobamos el valor de hideAction y devolvemos el nombre calificado
		// de la acción que vamos a ocultar o null, según el caso
		return hideAction ? "Order.createInvoice" : null;
	}

}

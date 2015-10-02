package org.openxava.invoicing.actions;

import org.openxava.actions.*;

/**
 * Extendemos la clase de OnChangePropertyBaseAction para poder hacer uso de @OnChange
 * Implementamos de IShowActionAction e IHideActionAction para poder mostrar u
 * ocultar una acción respectivamente
 * 
 * @author Informatica
 *
 */
public class ShowHideCreateInvoiceAction extends OnChangePropertyBaseAction
		implements IShowActionAction, IHideActionAction {

	/**
	 * Variable para controlar si se muestra o no la acción
	 * 'Order.createInvoice'. Si el valor de la variable es True se mostrará la
	 * acción
	 */
	private boolean show;

	/**
	 * Función que se ejecutará al realizar la acción correspondiente.
	 */
	public void execute() throws Exception {
		// Modificamos el valor de la variable show teniendo en cuenta tres
		// factores:
		// 1.- El pedido tiene que estar creado y almacenado en la base de datos
		// 2.- El pedido ha tenido que ser entregado
		// 3.- El pedido no tiene una factura asociada a el
		// Si estos 3 factores se cumplen el valor de show será verdadero y se
		// mostrará la acción, en caso contrario el valor de la variable show
		// será falso y la acción no estará disponible para el usuario
		show = isOrderCreated() && isDelivered() && !hasInvoice();
	}

	/**
	 * Función que nos permite saber si el pedido ha sido creado
	 * 
	 * @return True si el pedido está creado, False si aún no se ha creado
	 */
	private boolean isOrderCreated() {
		// Leemos el valor del identificador único de la vista y devolvemos True
		// si es distinto de null
		return getView().getValue("oid") != null;

	}

	/**
	 * Función para validar si el pedido ha sido enviado
	 * 
	 * @return True si el pedido ha sido enviado, False si no se ha enviado aún
	 */
	private boolean isDelivered() {

		// Almacenamos en una variable el valor del campo delivered del pedido
		Boolean delivered = (Boolean) getView().getValue("delivered");

		// Devolvemos False si no hemos recuperado el valor del campo delivered
		// de la vista y en caso contrario el valor que hayamos obtenido
		return delivered == null ? false : delivered;
	}

	/**
	 * Función que nos permite validar si un pedido tiene una factura asociada a
	 * él
	 * 
	 * @return True si el pedido tiene una factura asociada, False en caso
	 *         contrario
	 */
	private boolean hasInvoice() {
		// Leemos el identificador unico de la factura desde la vista, y
		// devolvemos True si es distinto de nulo (tiene factura asociada puesto
		// que podemos leer su identificador) y False en caso contrario
		return getView().getValue("invoice.oid") != null;
	}

	/**
	 * Función que muestra la acción para generar una factura del pedido
	 */
	public String getActionToShow() {
		// Comprobamos el valor de show y devolvemos el nombre calificado de la
		// acción a realizar o cadena vacía dependiendo de si debe mostrarse o
		// no
		return show ? "Order.createInvoice" : "";
	}

	/**
	 * Función que oculta la acción para generar una factura del pedido
	 */
	public String getActionToHide() {
		// Comprobamos el valor de show y devolvemos el nombre calificado de la
		// acción a realizar o cadena vacía dependiendo de si debe mostrarse o
		// no
		return !show ? "Order.createInvoice" : "";
	}
}
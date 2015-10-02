package org.openxava.invoicing.actions;

import java.util.*;

import javax.ejb.*;
import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.invoicing.model.*;
import org.openxava.model.*;

/**
 * Clase para realizar facturas a partir de la selecci�n de varios pedidos
 * Extiende TabBaseAction para poder usar getTab() Implementa
 * IChangeModuleAction para permitir pasar a otro m�dulo tras la creaci�n de las
 * facturas
 * 
 * @author Informatica
 *
 */
public class CreateInvoiceFromSelectedOrdersAction extends TabBaseAction
		implements IChangeModuleAction {

	@SuppressWarnings({ "rawtypes", "unused" })
	@Inject   // Usamos Inject para poder tener el valor de esta variable compartido por toda la aplicaci�n
	private Map currentInvoiceKey;

	/**
	 * Funci�n que va a ejecutarse cuando se realice la acci�n
	 */
	public void execute() throws Exception {

		// Recuperamos y almacenamos los pedidos seleccionados por el usuario
		Collection<Order> orders = getSelectedOrders();

		// Creamos las facturas a patir de los pedidos
		Invoice invoice = Invoice.createFromOrders(orders);

		// Mostramos un mensaje al usuario
		addMessage("invoice_created_from_orders", invoice, orders);

		// Volcamos la clave de la factura reci�n creada al campo
		// currentInvoiceKey, copiandose tambi�n en el objeto de sesi�n
		// invoicing_currentInvoiceKey
		currentInvoiceKey = toKey(invoice);
	}

	/**
	 * Funci�n que nos permite extraer la clave de la factura y devolver en
	 * formato mapa
	 * 
	 * @param invoice
	 *            La factura de la cual queremos extraer la clave
	 * @return La clave de la factura en formato mapa
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map toKey(Invoice invoice) {

		// Creamos un nuevo mapa
		Map key = new HashMap();

		// Almacenamos en el mapa el valor del id �nico de la factura
		key.put("oid", invoice.getOid());

		// Devolvemos la informaci�n recuperada
		return key;
	}

	/**
	 * Funci�n que nos permite recuperar los pedidos seleccionados por el
	 * usuario
	 * 
	 * @return Collection<Order> Una colecci�n de los pedidos seleccionados por
	 *         el usuario
	 * @throws FinderException
	 *             Una excepci�n si no puede encontrar ning�n pedido
	 *             seleccionado
	 */
	@SuppressWarnings("rawtypes")
	private Collection<Order> getSelectedOrders() throws FinderException {

		// Creamos una nueva colleci�n de pedidos a partir de un arraylist
		Collection<Order> result = new ArrayList<Order>();

		// Iteramos por todas las filas seleccionadas en la pantalla de lista
		for (Map key : getTab().getSelectedKeys()) {

			// Recuperamos de cada linea la entidad order y la asociamos a un
			// objeto Order. De este modo recuperamos la informaci�n total de
			// los pedidos a partir de la fila seleccionada.
			Order order = (Order) MapFacade.findEntity("Order", key);

			// A�adimos el pedido a la colecci�n de pedidos
			result.add(order);
		}

		// Devolvemos el resultado
		return result;
	}

	/**
	 * Funci�n que nos recuperar el siguiente m�dulo a ejecutar
	 */
	@Override
	public String getNextModule() {
		// Devolvemos el nombre del m�dulo que se ejecutar� tras realizar la
		// acci�n
		return "CurrentInvoiceEdition";
	}

	/**
	 * Funci�n que nos permite saber si el siguiente m�dulo debe inicializarse
	 * cada vez que cambiamos a �l
	 */
	@Override
	public boolean hasReinitNextModule() {
		// Devolvemos True para que el m�dulo se inialice cada vez que cambiemos
		// a �l.
		return false;
	}

}
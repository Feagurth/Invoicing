package org.openxava.invoicing.actions;

import java.util.*;

import javax.ejb.*;
import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.invoicing.model.*;
import org.openxava.model.*;

/**
 * Clase para realizar facturas a partir de la selección de varios pedidos
 * Extiende TabBaseAction para poder usar getTab() Implementa
 * IChangeModuleAction para permitir pasar a otro módulo tras la creación de las
 * facturas
 * 
 * @author Informatica
 *
 */
public class CreateInvoiceFromSelectedOrdersAction extends TabBaseAction
		implements IChangeModuleAction {

	@SuppressWarnings({ "rawtypes", "unused" })
	@Inject   // Usamos Inject para poder tener el valor de esta variable compartido por toda la aplicación
	private Map currentInvoiceKey;

	/**
	 * Función que va a ejecutarse cuando se realice la acción
	 */
	public void execute() throws Exception {

		// Recuperamos y almacenamos los pedidos seleccionados por el usuario
		Collection<Order> orders = getSelectedOrders();

		// Creamos las facturas a patir de los pedidos
		Invoice invoice = Invoice.createFromOrders(orders);

		// Mostramos un mensaje al usuario
		addMessage("invoice_created_from_orders", invoice, orders);

		// Volcamos la clave de la factura recién creada al campo
		// currentInvoiceKey, copiandose también en el objeto de sesión
		// invoicing_currentInvoiceKey
		currentInvoiceKey = toKey(invoice);
	}

	/**
	 * Función que nos permite extraer la clave de la factura y devolver en
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

		// Almacenamos en el mapa el valor del id único de la factura
		key.put("oid", invoice.getOid());

		// Devolvemos la información recuperada
		return key;
	}

	/**
	 * Función que nos permite recuperar los pedidos seleccionados por el
	 * usuario
	 * 
	 * @return Collection<Order> Una colección de los pedidos seleccionados por
	 *         el usuario
	 * @throws FinderException
	 *             Una excepción si no puede encontrar ningún pedido
	 *             seleccionado
	 */
	@SuppressWarnings("rawtypes")
	private Collection<Order> getSelectedOrders() throws FinderException {

		// Creamos una nueva colleción de pedidos a partir de un arraylist
		Collection<Order> result = new ArrayList<Order>();

		// Iteramos por todas las filas seleccionadas en la pantalla de lista
		for (Map key : getTab().getSelectedKeys()) {

			// Recuperamos de cada linea la entidad order y la asociamos a un
			// objeto Order. De este modo recuperamos la información total de
			// los pedidos a partir de la fila seleccionada.
			Order order = (Order) MapFacade.findEntity("Order", key);

			// Añadimos el pedido a la colección de pedidos
			result.add(order);
		}

		// Devolvemos el resultado
		return result;
	}

	/**
	 * Función que nos recuperar el siguiente módulo a ejecutar
	 */
	@Override
	public String getNextModule() {
		// Devolvemos el nombre del módulo que se ejecutará tras realizar la
		// acción
		return "CurrentInvoiceEdition";
	}

	/**
	 * Función que nos permite saber si el siguiente módulo debe inicializarse
	 * cada vez que cambiamos a él
	 */
	@Override
	public boolean hasReinitNextModule() {
		// Devolvemos True para que el módulo se inialice cada vez que cambiemos
		// a él.
		return false;
	}

}
package org.openxava.invoicing.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.validators.*;

/**
 * Clase para facturas que hereda de la clase CommercialDocument
 * 
 * @author Informatica
 *
 */
@Entity
@Views({
		@View(extendsView = "super.DEFAULT", members = "orders{orders}"),
		@View(name = "NoCustomerNoOrders", members = "year, number, date;"
				+ "details;" + "remarks") })
@Tabs({
		@Tab(baseCondition = "deleted = false", properties = "year, number, date, customer.number, customer.name, "
				+ "vatPercentage, estimatedProfit, baseAmount, "
				+ "vat, totalAmount, amount, remarks"), // Definimos la
														// condición para la
														// muestra de facturas
														// en el listado, así
														// como las columnas que
														// van a ser visibles

		@Tab(name = "Deleted", baseCondition = "deleted = true") })
// Definimos una tabla con nombre para mostrar la papelera de pedidos
public class Invoice extends CommercialDocument {

	/**
	 * Colección para almacenar los pedidos relacionados con la factura
	 */
	@OneToMany(mappedBy = "invoice")
	@CollectionView("NoCustomerNoInvoice")
	// Define nuestra propia acción para añadir pedidos
	private Collection<Order> orders;

	/**
	 * Función que nos permite recuperar los pedidos relacionados con la factura
	 * 
	 * @return Collection<Order> Una colección de pedidos relacionados con la
	 *         factura
	 */
	public Collection<Order> getOrders() {
		return orders;
	}

	/**
	 * Función que nos permite asignar una colección de ordenes a la factura
	 * 
	 * @param orders
	 *            La colección de pedidos que queremos relacionar con la factura
	 */
	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	/**
	 * Función que nos permite crear una factura a partir de varios pedidos
	 * 
	 * @param orders
	 *            Los pedidos a partir de los cuales se va a hacer la factura
	 * @return Invoice La factura resultante de los distintos pedidos
	 * @throws ValidationException
	 *             Si surge algún error se lanza una excepción
	 */
	public static Invoice createFromOrders(Collection<Order> orders)
			throws ValidationException {

		// Creamos un objeto Invoice para almacenar la factura resultante
		Invoice invoice = null;

		// Itermaos por todos los pedidos que se han pasado por parámetro
		for (Order order : orders) {

			// Si la factura es nula, es la primera vez que se trata un pedido
			if (invoice == null) {
				// Creamos la factura a partir del primer pedido
				order.createInvoice();

				// A partir del mismo pedido, recuperamos y almacenamos la
				// factura recién creada
				invoice = order.getInvoice();
			} else {
				// Para el resto de los pedidos la factura ya está creada y
				// podemos asignarla directamente al pedido
				order.setInvoice(invoice);

				// Usamos el método publico de Order copyDetailsToInvoice para
				// copiar la información del pedido a la factura
				order.copyDetailsToInvoice(invoice);
			}
		}

		// Comprobamos que se ha creado la factura
		if (invoice == null) {
			// Si a estas alturas no tenemos una factura creada, lanzamos una
			// excepción puesto que segurametne se haya generado un error
			throw new ValidationException(
					"impossible_create_invoice_orders_not_specified");
		}

		// Devolvemos la factura creada
		return invoice;
	}

}

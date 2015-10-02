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
														// condici�n para la
														// muestra de facturas
														// en el listado, as�
														// como las columnas que
														// van a ser visibles

		@Tab(name = "Deleted", baseCondition = "deleted = true") })
// Definimos una tabla con nombre para mostrar la papelera de pedidos
public class Invoice extends CommercialDocument {

	/**
	 * Colecci�n para almacenar los pedidos relacionados con la factura
	 */
	@OneToMany(mappedBy = "invoice")
	@CollectionView("NoCustomerNoInvoice")
	// Define nuestra propia acci�n para a�adir pedidos
	private Collection<Order> orders;

	/**
	 * Funci�n que nos permite recuperar los pedidos relacionados con la factura
	 * 
	 * @return Collection<Order> Una colecci�n de pedidos relacionados con la
	 *         factura
	 */
	public Collection<Order> getOrders() {
		return orders;
	}

	/**
	 * Funci�n que nos permite asignar una colecci�n de ordenes a la factura
	 * 
	 * @param orders
	 *            La colecci�n de pedidos que queremos relacionar con la factura
	 */
	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	/**
	 * Funci�n que nos permite crear una factura a partir de varios pedidos
	 * 
	 * @param orders
	 *            Los pedidos a partir de los cuales se va a hacer la factura
	 * @return Invoice La factura resultante de los distintos pedidos
	 * @throws ValidationException
	 *             Si surge alg�n error se lanza una excepci�n
	 */
	public static Invoice createFromOrders(Collection<Order> orders)
			throws ValidationException {

		// Creamos un objeto Invoice para almacenar la factura resultante
		Invoice invoice = null;

		// Itermaos por todos los pedidos que se han pasado por par�metro
		for (Order order : orders) {

			// Si la factura es nula, es la primera vez que se trata un pedido
			if (invoice == null) {
				// Creamos la factura a partir del primer pedido
				order.createInvoice();

				// A partir del mismo pedido, recuperamos y almacenamos la
				// factura reci�n creada
				invoice = order.getInvoice();
			} else {
				// Para el resto de los pedidos la factura ya est� creada y
				// podemos asignarla directamente al pedido
				order.setInvoice(invoice);

				// Usamos el m�todo publico de Order copyDetailsToInvoice para
				// copiar la informaci�n del pedido a la factura
				order.copyDetailsToInvoice(invoice);
			}
		}

		// Comprobamos que se ha creado la factura
		if (invoice == null) {
			// Si a estas alturas no tenemos una factura creada, lanzamos una
			// excepci�n puesto que segurametne se haya generado un error
			throw new ValidationException(
					"impossible_create_invoice_orders_not_specified");
		}

		// Devolvemos la factura creada
		return invoice;
	}

}

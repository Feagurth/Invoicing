package org.openxava.invoicing.model;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.beanutils.*;
import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.invoicing.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Clase para pedidos que hereda de la clase CommercialDocument
 * 
 * @author Informatica
 *
 */
@Entity
@Views({
		@View(extendsView = "super.DEFAULT", members = "delivered; invoice {invoice}"),
		@View(name = "NoCustomerNoInvoice", members = "year, number, date;"
				+ "details;" + "remarks") })
@Tabs({
// Definimos la condición para la muestra de pedidos en el listado, así como los
// datos que van a mostrarse en las columnas
		@Tab(baseCondition = "deleted = false", properties = "year, number, date, customer.number, customer.name, "
				+ "delivered, vatPercentage, estimatedProfit, baseAmount, "
				+ "vat, totalAmount, amount, remarks"),
		// Definimos una tabla con nombre para mostrar la papelera de pedidos
		@Tab(name = "Deleted", baseCondition = "deleted = true") })
public class Order extends CommercialDocument {

	/**
	 * Variable para almacenar la relación entre un pedido y una factura
	 */
	@ManyToOne
	@ReferenceView("NoCustomerNoOrders")
	@OnChange(ShowHideCreateInvoiceAction.class)
	// Define la acción para buscar facturas
	@SearchAction("Order.searchInvoice")
	// Define la clase que se usará para buscar facturas al cambiar de campo
	@OnChangeSearch(OnChangeSearchInvoiceAction.class)
	private Invoice invoice;

	/**
	 * Variable que nos permite controlar si el pedido ha sido enviado o no
	 */
	@OnChange(ShowHideCreateInvoiceAction.class)
	// Se realizan las acciones de la clase especificada cuando se detectan
	// cambios en el valor de la variable
	private boolean delivered;

	/**
	 * Función que nos permite recuperar la factura a la que está ligado el
	 * pedido
	 * 
	 * @return Invoice La factura a la que está ligada el pedido
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * Función que nos permite asignar un pedido a una factura específica
	 * 
	 * @param invoice
	 *            La factura que queremos asignar el pedido
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * Función que nos permite verificar si el pedido ha sido enviado o no
	 * 
	 * @return True si el pedido ha sido enviado, false en caso contrario
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * Función que nos permite asignar a un pedido el estado de enviado
	 * 
	 * @param delivered
	 *            True si el pedido ha sido enviado, False en caso contrario
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * Función que nos ayuda a validar si el pedido puede ser añadido a una
	 * factura
	 * 
	 * @throws Exception
	 *             Lanza una excepción si se produce un error
	 */
	@PreUpdate
	// Realizamos la validación cada vez que se vaya a realizar una
	// actualización de los datos
	private void validate() throws Exception {
		// Comprobamos que tenemos una factura y de ser así comprobamos si el
		// pedido ha sido enviado
		if (invoice != null && !isDelivered()) {
			// Si ninguna de estas dos condiciones se cumple, lanzamos una
			// excepción con un mensaje definido
			throw new InvalidStateException(
					new InvalidValue[] { new InvalidValue(XavaResources
							.getString("cannot_add_order_without_sending"),
							getClass(), "delivered", true, this) });
		}
	}

	/**
	 * Función para validar que un pedido no tenga una factura asociada
	 */
	@PreRemove
	// Realizamos la validación antes de realizar el borrado
	private void validateOnRemove() {
		// Comprobamos que tenemos una factura
		if (invoice != null) {
			// Si es así, estamos intentado eliminar un pedido asociado a una
			// factura, lo cual no es válido, por tanto lanzamos una excepción
			throw new InvalidStateException(
					new InvalidValue[] { new InvalidValue(XavaResources
							.getString("cannot_delete_order_with_invoice"),
							getClass(), "delivered", true, this) });
		}

	}

	/**
	 * Función para realizar el borrado de pedidos
	 */
	public void setDeleted(boolean deleted) {
		// Comprobamos si se el pedido ha sido borrado
		if (deleted) {
			// Si es así llamamos explicitamente a la función anterior para que
			// se encargue de comprobar si tenemos una factura asociada al
			// pedido
			validateOnRemove();
		}
		// Si todo va bien, marcamos el pedido como borrado
		super.setDeleted(deleted);
	}

	/**
	 * Función para crear una factura a partir de un pedido
	 * 
	 * @throws Exception
	 *             Se lanza una excepción si se ha producido un error
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createInvoice() throws ValidationException {

		// Comprobamos si el pedido ya tiene asociada una factura
		if (this.invoice != null) {
			// De ser así, lanzamos una excepción de validación con un mensaje
			// especificado
			throw new ValidationException(
					"impossible_create_invoice_order_already_has_one");
		}

		// Comprobamos is el pedido ha sido enviado
		if (!isDelivered()) {
			// De ser así, lanzamos una excepción de validación con un mensaje
			// especificado
			throw new ValidationException(
					"impossible_create_invoice_order_is_not_delivered");
		}

		try {
			// Instanaciamos una factura
			Invoice invoice = new Invoice();

			// Copiamos las propiedades del pedido actual a la factura recién
			// creada
			BeanUtils.copyProperties(invoice, this);

			// Ponemos el id a nulo para asegurar que al guardarse la factura en
			// la
			// base de datos, se genere un oid nuevo y no se guarde con el oid
			// del
			// pedido desde el que se genera
			invoice.setOid(null);

			// Asignamos la fecha actual a la factura
			invoice.setDate(new Date());

			// Borramos los detlles que se hayan podido copiar del pedido actual
			invoice.setDetails(new ArrayList());

			// Almacenamos la nueva factura en la base de datos
			XPersistence.getManager().persist(invoice);

			// Rellenamos la colección de detalles
			copyDetailsToInvoice(invoice);

			// Asignamos la factura recién creada al pedido actual, siempre tras
			// almacenarla en la base de datos
			this.invoice = invoice;
		} catch (Exception ex) {
			// Si se produce un error, lanzamos una excepción de sistema con la
			// traza del error
			throw new SystemException("impossible_crear_invoice", ex);
		}
	}

	/**
	 * Función que nos permite copiar los detalles de un pedido a una factura
	 * Hacemos el método publico para que pueda ser usado posteriormente en el
	 * objeto Invoice para realizar una factura a partir de varios pedidos
	 * aprovechando así la lógica de la aplicación
	 * 
	 * @param invoice
	 *            Factura sobre la que se van a copiar los detalles del pedido
	 *            actual
	 */
	public void copyDetailsToInvoice(Invoice invoice) {

		try {
			// Iteramos por todos los detalles del pedido
			for (Detail orderDetail : getDetails()) {

				// Clonamos el detalle de la iteración a uno nuevo
				Detail invoiceDetail = (Detail) BeanUtils
						.cloneBean(orderDetail);

				// Anulamos el identificador para no tener problemas con JPA y
				// que
				// se lo adjudique por si mismo
				invoiceDetail.setOid(null);

				// Asignamos como padre de la linea de detalle la factura sobre
				// la
				// que estamos trabajando
				invoiceDetail.setParent(invoice);

				// Guardamos el detalle en la base de datos
				XPersistence.getManager().persist(invoiceDetail);
			}
		} catch (Exception ex) {
			throw new SystemException("impossible_copy_details_to_invoice", ex);
		}
	}

	/**
	 * Función que nos permite copiar los detalles de un pedido a una factura
	 */
	public void copyDetailsToInvoice() {

		// Hacemos uso de la función anterior recuperando la factura del propio
		// pedido
		copyDetailsToInvoice(getInvoice());
	}

	/**
	 * Función que nos permite si el cliente de un pedido y su factura asociada
	 * son el mismo
	 * 
	 * @return True si el cliente es el mismo, False en caso contrario
	 */
	@AssertTrue
	private boolean isCustomerOfInvoiceMustBeTheSame() {

		// Verificamos que no haya factura asociada puesto que es opcional, pero
		// de haberla, comprobamos los números del cliente de la factura y del
		// pedido para ver si son iguales
		return invoice == null
				|| invoice.getCustomer().getNumber() == getCustomer()
						.getNumber();

	}
}

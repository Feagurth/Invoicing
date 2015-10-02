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
// Definimos la condici�n para la muestra de pedidos en el listado, as� como los
// datos que van a mostrarse en las columnas
		@Tab(baseCondition = "deleted = false", properties = "year, number, date, customer.number, customer.name, "
				+ "delivered, vatPercentage, estimatedProfit, baseAmount, "
				+ "vat, totalAmount, amount, remarks"),
		// Definimos una tabla con nombre para mostrar la papelera de pedidos
		@Tab(name = "Deleted", baseCondition = "deleted = true") })
public class Order extends CommercialDocument {

	/**
	 * Variable para almacenar la relaci�n entre un pedido y una factura
	 */
	@ManyToOne
	@ReferenceView("NoCustomerNoOrders")
	@OnChange(ShowHideCreateInvoiceAction.class)
	// Define la acci�n para buscar facturas
	@SearchAction("Order.searchInvoice")
	// Define la clase que se usar� para buscar facturas al cambiar de campo
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
	 * Funci�n que nos permite recuperar la factura a la que est� ligado el
	 * pedido
	 * 
	 * @return Invoice La factura a la que est� ligada el pedido
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * Funci�n que nos permite asignar un pedido a una factura espec�fica
	 * 
	 * @param invoice
	 *            La factura que queremos asignar el pedido
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * Funci�n que nos permite verificar si el pedido ha sido enviado o no
	 * 
	 * @return True si el pedido ha sido enviado, false en caso contrario
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * Funci�n que nos permite asignar a un pedido el estado de enviado
	 * 
	 * @param delivered
	 *            True si el pedido ha sido enviado, False en caso contrario
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * Funci�n que nos ayuda a validar si el pedido puede ser a�adido a una
	 * factura
	 * 
	 * @throws Exception
	 *             Lanza una excepci�n si se produce un error
	 */
	@PreUpdate
	// Realizamos la validaci�n cada vez que se vaya a realizar una
	// actualizaci�n de los datos
	private void validate() throws Exception {
		// Comprobamos que tenemos una factura y de ser as� comprobamos si el
		// pedido ha sido enviado
		if (invoice != null && !isDelivered()) {
			// Si ninguna de estas dos condiciones se cumple, lanzamos una
			// excepci�n con un mensaje definido
			throw new InvalidStateException(
					new InvalidValue[] { new InvalidValue(XavaResources
							.getString("cannot_add_order_without_sending"),
							getClass(), "delivered", true, this) });
		}
	}

	/**
	 * Funci�n para validar que un pedido no tenga una factura asociada
	 */
	@PreRemove
	// Realizamos la validaci�n antes de realizar el borrado
	private void validateOnRemove() {
		// Comprobamos que tenemos una factura
		if (invoice != null) {
			// Si es as�, estamos intentado eliminar un pedido asociado a una
			// factura, lo cual no es v�lido, por tanto lanzamos una excepci�n
			throw new InvalidStateException(
					new InvalidValue[] { new InvalidValue(XavaResources
							.getString("cannot_delete_order_with_invoice"),
							getClass(), "delivered", true, this) });
		}

	}

	/**
	 * Funci�n para realizar el borrado de pedidos
	 */
	public void setDeleted(boolean deleted) {
		// Comprobamos si se el pedido ha sido borrado
		if (deleted) {
			// Si es as� llamamos explicitamente a la funci�n anterior para que
			// se encargue de comprobar si tenemos una factura asociada al
			// pedido
			validateOnRemove();
		}
		// Si todo va bien, marcamos el pedido como borrado
		super.setDeleted(deleted);
	}

	/**
	 * Funci�n para crear una factura a partir de un pedido
	 * 
	 * @throws Exception
	 *             Se lanza una excepci�n si se ha producido un error
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createInvoice() throws ValidationException {

		// Comprobamos si el pedido ya tiene asociada una factura
		if (this.invoice != null) {
			// De ser as�, lanzamos una excepci�n de validaci�n con un mensaje
			// especificado
			throw new ValidationException(
					"impossible_create_invoice_order_already_has_one");
		}

		// Comprobamos is el pedido ha sido enviado
		if (!isDelivered()) {
			// De ser as�, lanzamos una excepci�n de validaci�n con un mensaje
			// especificado
			throw new ValidationException(
					"impossible_create_invoice_order_is_not_delivered");
		}

		try {
			// Instanaciamos una factura
			Invoice invoice = new Invoice();

			// Copiamos las propiedades del pedido actual a la factura reci�n
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

			// Rellenamos la colecci�n de detalles
			copyDetailsToInvoice(invoice);

			// Asignamos la factura reci�n creada al pedido actual, siempre tras
			// almacenarla en la base de datos
			this.invoice = invoice;
		} catch (Exception ex) {
			// Si se produce un error, lanzamos una excepci�n de sistema con la
			// traza del error
			throw new SystemException("impossible_crear_invoice", ex);
		}
	}

	/**
	 * Funci�n que nos permite copiar los detalles de un pedido a una factura
	 * Hacemos el m�todo publico para que pueda ser usado posteriormente en el
	 * objeto Invoice para realizar una factura a partir de varios pedidos
	 * aprovechando as� la l�gica de la aplicaci�n
	 * 
	 * @param invoice
	 *            Factura sobre la que se van a copiar los detalles del pedido
	 *            actual
	 */
	public void copyDetailsToInvoice(Invoice invoice) {

		try {
			// Iteramos por todos los detalles del pedido
			for (Detail orderDetail : getDetails()) {

				// Clonamos el detalle de la iteraci�n a uno nuevo
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
	 * Funci�n que nos permite copiar los detalles de un pedido a una factura
	 */
	public void copyDetailsToInvoice() {

		// Hacemos uso de la funci�n anterior recuperando la factura del propio
		// pedido
		copyDetailsToInvoice(getInvoice());
	}

	/**
	 * Funci�n que nos permite si el cliente de un pedido y su factura asociada
	 * son el mismo
	 * 
	 * @return True si el cliente es el mismo, False en caso contrario
	 */
	@AssertTrue
	private boolean isCustomerOfInvoiceMustBeTheSame() {

		// Verificamos que no haya factura asociada puesto que es opcional, pero
		// de haberla, comprobamos los n�meros del cliente de la factura y del
		// pedido para ver si son iguales
		return invoice == null
				|| invoice.getCustomer().getNumber() == getCustomer()
						.getNumber();

	}
}

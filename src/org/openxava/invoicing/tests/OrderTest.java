package org.openxava.invoicing.tests;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.invoicing.model.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * Clase para realizar pruebas a la clase Order
 * 
 * @author Informatica
 *
 */
public class OrderTest extends CommercialDocumentTest {

	/**
	 * El constructor de a clase
	 * 
	 * @param testName
	 *            El nombre del test a realizar
	 */
	public OrderTest(String testName) {
		super(testName, "Order");
	}

	/**
	 * Funci�n que nos permite comprobar el alta y baja de facturas desde un
	 * pedido
	 * 
	 * @throws Exception
	 */
	public void testSetInvoice() throws Exception {

		// Comprobamos que la lista no est� vac�a
		assertListNotEmpty();

		// Establecemos el orden de la lista
		execute("List.orderBy", "property=number");

		// Vamos al modo de detalle del primer registro
		execute("Mode.detailAndFirst");

		// El pedido no ha de estar entregado
		assertValue("delivered", "false");

		// Cambiamos a la pesta�a 1
		execute("Sections.change", "activeSection=1"); // Cambia a la pesta�a 1

		// Comprobamos que le pedido no tiene factura asignada
		assertValue("invoice.number", "");
		assertValue("invoice.year", "");

		// Pulsamos el bot�n de buscar factura para pasar a la lista de facturas
		execute("Reference.search", "keyProperty=invoice.year");

		// Almacenamos el a�o y el n�mero de la primera factura que haya en la
		// lista
		String year = getValueInList(0, "year");
		String number = getValueInList(0, "number");

		// Seleccionamos la primera factura
		execute("ReferenceSearch.choose", "row=0");

		// Verificamos que el a�o y el n�mero de la factura seleccionada son
		// iguales a los que tenemos almacenados
		assertValue("invoice.year", year);
		assertValue("invoice.number", number);

		// Los pedidos no entregados no pueden tener factura
		execute("CRUD.save");
		assertErrorsCount(1); // No podemos grabar porque no ha sido entregado
		setValue("delivered", "true");
		execute("CRUD.save"); // Con delivered=true podemos grabar el pedido
		assertNoErrors();

		// Un pedido con factura no se puede borrar
		execute("Mode.list"); // Vamos al modo lista y
		execute("Mode.detailAndFirst"); // volvemos a detalle para cargar el
										// pedido grabado
		execute("Invoicing.delete"); // No podemos borrar porque tiene una
										// factura
		// asociada

		assertErrorsCount(1);

		// Restaurar los valores originales
		setValue("delivered", "false");
		setValue("invoice.year", "");
		execute("CRUD.save");
		assertNoErrors();
	}

	/**
	 * Funci�n para probar la creaci�n de facturas desde la vista detalle de un
	 * pedido
	 * 
	 * @throws Exception
	 */
	public void testCreateInvoiceFromOrder() throws Exception {

		// Buscar un pedido del que se pueda hacer una factura
		searchOrderSusceptibleToBeInvoiced();

		// Comprobamos que el pedido est� entregado
		assertValue("delivered", "true");

		// Almacenamos la cantidad de lineas de detalle que tiene el pedido
		int orderDetailsCount = getCollectionRowCount("details");

		// Cambiamos a la secci�n de la factura
		execute("Sections.change", "activeSection=1");

		// Verificamos que no haya una factura creada con anterioridad,
		// comprobando que no carga ning�n a�o ni n�mero.
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");

		// Creamos la factura ejecutando la funci�n que estamos probando
		execute("Order.createInvoice");

		// Almacenamos el valor del a�o de la factura
		String invoiceYear = getValue("invoice.year");

		// Comprobamos que la factura existe en su pesta�a correspondiente
		assertTrue("Invoice year must have value", !Is.emptyString(invoiceYear));

		// Almacenamos el n�mero de la factura
		String invoiceNumber = getValue("invoice.number");

		// Verficamos el n�mero de la factura
		assertTrue("Invoice number must have value",
				!Is.emptyString(invoiceNumber));

		// Comprobamos que se ha creado la factura verificando el mensaje que se
		// muestra al usuario
		assertMessage("Factura " + invoiceYear + "/" + invoiceNumber
				+ " creada para el pedido actual");

		// Verificamos que la cantidad de lineas de detalle de la factura se
		// corresponde con el numero de lineas de detalle del pedido
		assertCollectionRowCount("invoice.details", orderDetailsCount);

		// Restauramos el pedido para poder ejecutar la prueba la siguiente vez
		setValue("invoice.year", "");
		assertValue("invoice.number", "");
		assertCollectionRowCount("invoice.details", 0);
		execute("CRUD.save");
		assertNoErrors();
	}

	/**
	 * Funci�n que nos permite buscar un pedido del cual se pueda hacer una
	 * factura
	 */
	private void searchOrderSusceptibleToBeInvoiced() throws Exception {
		// Pasamos la a la funci�n que se va a encargar de la b�squeda la
		// condici�n, en este caso buscamos por un pedido entregado y sin
		// factura asociada
		searchOrderUsingList("o.delivered = true and o.invoice = null");
	}

	/**
	 * Funci�n que se encarga de buscar pedidos usando el listado con una
	 * condici�n espec�fica
	 * 
	 * @param condition
	 *            La condici�n de busqueda de los pedidos
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	private void searchOrderUsingList(String condition) throws Exception {

		// Buscamos el pedido usando JPA
		Order order = findOrder(condition);

		// Almacenamos el a�o y el n�mero del pedido
		String year = String.valueOf(order.getYear());
		String number = String.valueOf(order.getNumber());

		// Rellena con el a�o y el n�mero recuperados los campos
		setConditionValues(new String[] { year, number });

		// Pulsa el bot�n de filtrar en la lista para buscar los pedidos que se
		// correspondan con la condici�n especificada
		execute("List.filter");

		// Comprobamosq ue la lista filtrada tenga el menos un registro
		assertListRowCount(1);

		// Entramos en modo detalle
		execute("Mode.detailAndFirst");

		// Verificamos que el a�o y el n�mero de pedido sean los correctos
		assertValue("year", year);
		assertValue("number", number);
	}

	/**
	 * Funci�n que nos permite buscar un pedido con unas condiciones espec�ficas
	 * 
	 * @param condition
	 *            La condici�n de b�squeda del pedido
	 * @return El pedido que cumple las condiciones espec�ficas
	 */
	@SuppressWarnings("rawtypes")
	private Order findOrder(String condition) {

		// Creamos una consulta para recuperar los pedidos de la base de datos
		// que cumplan con la condici�n, excluyendo eso s� aquellos que han sido
		// borrados con anterioridad
		Query query = XPersistence.getManager().createQuery(
				"from Order o where o.deleted = false and " + condition);

		// Recuperamos los resultados de la consulta y los almacenamos en una
		// lista
		List orders = query.getResultList();

		// Comprobamos que al menos tenemos un resultado para poder seguir con
		// la prueba
		if (orders.isEmpty()) {
			// Si no es as�, damos la prueba como fallida y mostramos un mensaje
			fail("To run this test you must have some order with " + condition);
		}

		// Devolvemos el primer registro de la lista de pedidos que ha devuelto
		// la consulta de base de datos
		return (Order) orders.get(0);
	}

	public void testHidesCreateInvoiceFromOrderWhenNotApplicable()
			throws Exception {

		// Buscamos un pedido que haya sido enviado y que tengan una factura
		// asociada
		searchOrderUsingList("delivered = true and invoice <> null");

		// Comprobamos que no se puede pulsar el bot�n de crear factura
		assertNoAction("Order.createInvoice");

		// Pasamos al modo lista
		execute("Mode.list");

		// Buscamos pedidos que no hayan sido entregados y que no tengan factura
		searchOrderUsingList("delivered = false and invoice = null");

		// Comprobamos que no se puede pulsar el bot�n de crear factura
		assertNoAction("Order.createInvoice");

		// Creamos un pedido nuevo, para comprobar que no aparece el bot�n de
		// crear facturas si el pedido no est� grabado
		execute("CRUD.new");

		// Comprobamos que no se puede pulsar el bot�n de crear factura
		assertNoAction("Order.createInvoice");
	}

	/**
	 * Funci�n para verificar la creaci�n de facturas despues de seleccionar
	 * varios pedidos
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	public void testCreateInvoiceFromSelectedOrders() throws Exception {

		// Verificamos el pedido
		assertOrder(2015, 4, 1, 30);

		// Verificamos el pedido
		assertOrder(2015, 2, 1, 40);

		// Ordenamos la lista por n�mero
		execute("List.orderBy", "property=number");

		// Marcamos los pedidos en la lista
		checkRow(getDocumentRowInList("2015", "4"));
		checkRow(getDocumentRowInList("2015", "2"));

		// Creamos la factura a partir de los pediso seleccionados
		execute("Order.createInvoiceFromSelectedOrders");

		// Almacenamos el valor del a�o y el n�mero de la factura
		String invoiceYear = getValue("year");
		String invoiceNumber = getValue("number");

		// Comprobamos que le mensaje que se muestra es el que se debe
		assertMessage("Factura " + invoiceYear + "/" + invoiceNumber
				+ " creada a partir de la orden: [2015/4, 2015/2]");

		// Contamos el n�mero de filas que hay
		assertCollectionRowCount("details", 2);

		// Comprobamos el importe base de la factura
		assertValue("baseAmount", "70,00");

		// Cambiamos a la secci�n de pedidos
		execute("Sections.change", "activeSection=1");

		// Comprobamos que la factura est� formada por dos ordenes
		assertCollectionRowCount("orders", 2);

		// Verificamos que los valores de los pedidos son los correctos
		assertValueInCollection("orders", 0, 0, "2015");
		assertValueInCollection("orders", 0, 1, "4");
		assertValueInCollection("orders", 1, 0, "2015");
		assertValueInCollection("orders", 1, 1, "2");

		// Comprobmaos los botones de grabaci�n y de volver
		assertAction("CurrentInvoiceEdition.save");
		assertAction("CurrentInvoiceEdition.return");

		// Marcamos los primeros dos pedidos
		checkRowCollection("orders", 0);
		checkRowCollection("orders", 1);

		// Eliminamos los pedidos seleccionados
		execute("Collection.removeSelected",
				"viewObject=xava_view_section1_orders");

		// Comprobamos que no se hayan producido errores
		assertNoErrors();

		// Volvemos a la vista de pedidos
		execute("CurrentInvoiceEdition.return");

		assertDocumentInList("2015", "4");
		assertDocumentInList("2015", "2");
	}

	/**
	 * Funci�n para verificar la informaci�n de un pedido
	 * 
	 * @param year
	 *            El a�o del pedido
	 * @param number
	 *            El n�mero del pedido
	 * @param detailsCount
	 *            El n�mero de lineas de detalle del pedido
	 * @param baseAmount
	 *            El monto base del pedido
	 */
	private void assertOrder(int year, int number, int detailsCount,
			int baseAmount) {

		// Buscamos el pedido
		Order order = findOrder("year = " + year + " and number=" + number);

		assertEquals("Para ejecutar este test el pedido " + order
				+ " debe tener  " + detailsCount + " linea/s de detalle",
				detailsCount, order.getDetails().size());

		assertTrue(
				"Para ejecutar este test el pedido " + order + " debe tener "
						+ baseAmount + " como monto base del mismo",
				order.getBaseAmount().compareTo(new BigDecimal(baseAmount)) == 0);
	}

	/**
	 * Funci�n para probar los casos excepcionales de creaci�n de facturas a
	 * partir de pedidos
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	public void testCreateInvoiceFromOrderExceptions() throws Exception {

		// Comprobamos que no se puede crear una factura a partir de un pedido
		// enviado con una factura asociada
		assertCreateInvoiceFromOrderException(
				"delivered = true and invoice <> null",
				"Imposible crear factura: El pedido ya tiene una asignada");

		// Verificamos que no se puede crear una factura a partir de un pedido
		// sin factura que no se ha enviado
		assertCreateInvoiceFromOrderException(
				"delivered = false and invoice = null",
				"Imposible crear factura: El pedido no ha sido enviado");
	}

	/**
	 * Funci�n que nos permite localizar un pedido a partir de una condici�n
	 * espec�fica
	 * 
	 * @param condition
	 *            La condici�n para localizar el pedido deseado
	 * @param message
	 *            El mensaje contra el que validar el resultado
	 * @throws Exception
	 *             Si se produce alg�n error se lanza una excepci�n
	 */
	private void assertCreateInvoiceFromOrderException(String condition,
			String message) throws Exception {

		// Buscamos el pedido a partir de la condici�n que se nos pasa como
		// par�metro
		Order order = findOrder(condition);

		// Recuperamos el n�mero de fila del pedido
		int row = getDocumentRowInList(String.valueOf(order.getYear()),
				String.valueOf(order.getNumber()));

		// Marcamos la fila
		checkRow(row);

		// Pulsamos el bot�n de creaci�n de factura desde los pedidos marcados
		execute("Order.createInvoiceFromSelectedOrders");

		// Comprobamos que el mensaje es el mismo que nos ha llegado por el
		// mensaje
		assertError(message);

		// Desmarcamos la fila
		uncheckRow(row);
	}
}

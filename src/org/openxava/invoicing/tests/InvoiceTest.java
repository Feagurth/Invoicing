package org.openxava.invoicing.tests;

/**
 * Clase para realizar pruebas a la clase Invoice
 * 
 * @author Informatica
 *
 */
public class InvoiceTest extends CommercialDocumentTest {

	/**
	 * Constructor de la clasae
	 * 
	 * @param testName
	 *            El nombre del test a realizar
	 */
	public InvoiceTest(String testName) {
		super(testName, "Invoice");
	}

	/**
	 * Función que nos permite verificar los procesos de añadir y borrar pedidos
	 * 
	 * @throws Exception
	 */
	public void testAddOrders() throws Exception {

		// Comprobamos que existan facturas
		assertListNotEmpty();

		// Ordenamos la lista por número
		execute("List.orderBy", "property=number");

		// Vamos al modo de detalle de la primera factura
		execute("Mode.detailAndFirst");

		// Almacenamos el número de cliente
		String customerNumber = getValue("customer.number");

		// Borra las lineas de detalle de una factura si las hay
		deleteDetails();

		// Comprobamos que no hay detalles en la factura
		assertCollectionRowCount("details", 0);

		// Comprobamos que el monto básico es nulo
		assertValue("baseAmount", "0,00");

		// Cambiamos a la pestaña 1
		execute("Sections.change", "activeSection=1");

		// Comprobamos que la factura no tenga pedidos
		assertCollectionRowCount("orders", 0);

		// Pulsamos el botón de añadir nuevos pedidos
		execute("Invoice.addOrders", "viewObject=xava_view_section1_orders");

		// Añadimos el pedido a la factura
		execute("AddOrdersToInvoice.add", "row=0");

		// Verificamos que todos los pedidos en la lista tienen el mismo cliente
		assertCustomerInList(customerNumber);

		// Comprobamos que el valor de la columna envío sea sí para todas las
		// filas
		assertValueForAllRows(5, "Sí");

		// Tomamos nota del importe base del primer pedido de la lista
		String firstOrderBaseAmount = getValueInList(0, 8);

		// Almacenamos el número de pedidos que tenemos en la lista
		int ordersRowCount = getListRowCount();

		// Comprobamos que tenemos un mensaje de elemento añadido correctamente
		// por el pedido entregado
		assertMessage("1 elemento(s) añadido(s) a Pedidos de Factura");

		// Comprobamos que tenemos una linea nueva del pedido que acabamos de
		// añador
		assertCollectionRowCount("orders", 1);

		// Cambiamos de sección
		execute("Sections.change", "activeSection=0");

		// Comprobamos que se han copiado los detalles
		assertCollectionNotEmpty("details");

		// Volvemos a la sección en la que estabamos inicialmente
		execute("Sections.change", "activeSection=1");

		// Verificamos que el importe base de la factura y la almacenada son
		// iguales
		assertValue("baseAmount", firstOrderBaseAmount);

		// Mostramos la lista de pedidos de nuevo
		execute("Invoice.addOrders", "viewObject=xava_view_section1_orders");

		// Verificamos que la lista de pedidos tiene un elemento menos
		assertListRowCount(ordersRowCount - 1);

		// Cancelamos para cerrar la lista de pedidos
		execute("AddToCollection.cancel");

		// Marcamos el pedido que acabamos de añadir
		checkRowCollection("orders", 0);

		// Y lo borramos
		execute("Collection.removeSelected",
				"viewObject=xava_view_section1_orders");

		// Comprobamos que el pedido se haya borrado comprobando que el número
		// de lineas en nulo
		assertCollectionRowCount("orders", 0);
	}

	/**
	 * Funcion para eliminar los detalles de una factura
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	private void deleteDetails() throws Exception {

		// Recuperamos el número de lineas de detalle
		int c = getCollectionRowCount("details");

		// Iteramos por todas las filas de detalle
		for (int i = 0; i < c; i++) {
			// Vamos marcando las filas
			checkRowCollection("details", i);
		}

		// Borramos las filas marcadas
		execute("Collection.removeSelected",
				"viewObject=xava_view_section0_details");
	}
}

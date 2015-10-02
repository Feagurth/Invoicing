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
	 * Funci�n que nos permite verificar los procesos de a�adir y borrar pedidos
	 * 
	 * @throws Exception
	 */
	public void testAddOrders() throws Exception {

		// Comprobamos que existan facturas
		assertListNotEmpty();

		// Ordenamos la lista por n�mero
		execute("List.orderBy", "property=number");

		// Vamos al modo de detalle de la primera factura
		execute("Mode.detailAndFirst");

		// Cambiamos a la pesta�a 1
		execute("Sections.change", "activeSection=1");

		// Comprobamos que la factura no tenga pedidos
		assertCollectionRowCount("orders", 0);

		// Pulsamos el bot�n de a�adir nuevos pedidos
		execute("Collection.add", "viewObject=xava_view_section1_orders");

		// Seleccionamos un pedido que haya sido entregado
		checkFirstOrderWithDeliveredEquals("S�");

		// Seleccionamos un pedido que no haya sido entregado
		checkFirstOrderWithDeliveredEquals("No");

		// Tratamos de a�adir los dos
		execute("AddToCollection.add");

		// Comprobamos que surga un error al intentar a�adir un pedido no
		// entregado
		assertError("�ERROR! 1 elemento(s) NO a�adido(s) a Pedidos de Factura");

		// Comprobamos que tenemos un mensaje de elemento a�adido correctamente
		// por el pedido entregado
		assertMessage("1 elemento(s) a�adido(s) a Pedidos de Factura");

		// Comprobamos que tenemos una linea nueva del pedido que acabamos de
		// a�ador
		assertCollectionRowCount("orders", 1);

		// Marcamos el pedido que acabamos de a�adir
		checkRowCollection("orders", 0);

		// Y lo borramos
		execute("Collection.removeSelected",
				"viewObject=xava_view_section1_orders");

		// Comprobamos que el pedido se haya borrado comprobando que el n�mero
		// de lineas en nulo
		assertCollectionRowCount("orders", 0);
	}

	private void checkFirstOrderWithDeliveredEquals(String value)
			throws Exception {
		int c = getListRowCount(); // El total de filas visualizadas en la lista
		for (int i = 0; i < c; i++) {
			if (value.equals(getValueInList(i, 2))) // 2 es la columna
													// 'delivered'
			{
				checkRow(i);
				return;
			}
		}
		fail("Must be at least one row with delivered=" + value);
	}

}

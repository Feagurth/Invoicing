package org.openxava.invoicing.tests;

import static org.openxava.jpa.XPersistence.getManager; // Para usar JPA

import java.text.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * Clase de pruebas para documentos comerciales
 * 
 * @author Informatica
 *
 */
abstract public class CommercialDocumentTest extends ModuleTestBase {

	// Para almacenar el número del documento que probamos
	private String number;

	// Para almacenar el modelo de documento comercial sobre el que se está
	// haciendo la prueba
	private String model;

	/**
	 * Constructor de la clase de pruebas
	 * 
	 * @param testName
	 *            El nombre de la prueba
	 */
	public CommercialDocumentTest(String testName, String moduleName) {
		super(testName, "Invoicing", moduleName);
		this.model = moduleName;
	}

	/**
	 * Función de prueba para la creación de documentos
	 * 
	 * @throws Exception
	 */
	public void testCreate() throws Exception {

		// Calculamos inicialmente el número que tendrá el documento en la base
		// de datos para verificar que se calcula correctamente a posteriori
		calculateNumber();

		// Verificamos los valores por defecto de una nueva factura
		verifyDefaultValues();

		// Seleccionamos un cliente
		chooseCustomer();

		// Añadimos los detalles
		addDetails();

		// Asignamos otras propiedades
		setOtherProperties();

		// Grabamos la factura
		save();

		// Prueba el método de retrollamda y @formula
		verifyAmountAndEstimatedProfit();

		// Verificamos que se ha creado correctamente
		verifyCreated();

		// Eliminamos la factura
		remove();
	}

	/**
	 * Función que nos permite verificar que la papelera de pedidos y de
	 * facturas funciona correctametne
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	public void testTrash() throws Exception {

		// Verificamos que solamente hay una pagina en la lista, esto es, que
		// hay menos de 10 registros
		assertListOnlyOnePage();

		// Almacenamos en número de registros que hay
		int initialRowCount = getListRowCount();

		// Almacenamos el año y el número del primer registro en la lista
		String year1 = getValueInList(0, 0);
		String number1 = getValueInList(0, 1);

		// Pasamos a modo detalle
		execute("Mode.detailAndFirst");

		// Eliminamos el registro
		execute("Invoicing.delete");

		// Pasamos de nuevo a modo lista
		execute("Mode.list");

		// Verificamos que hay una fila menos tras el borrado del registro
		assertListRowCount(initialRowCount - 1);

		// Verificamos que la entidad borrada no está en la lista
		assertDocumentNotInList(year1, number1);

		// Almacenamos el año y el número del siguiente registro en la lista
		String year2 = getValueInList(0, 0);
		String number2 = getValueInList(0, 1);

		// Marcamos la primera fila de la lista
		checkRow(0);

		// La borramos
		execute("Invoicing.deleteSelected");

		// Verificamos que hay dos filas menos en la lista
		assertListRowCount(initialRowCount - 2);

		// Comprobamos que el registro que acabamos de borrar no está en la
		// lista
		assertDocumentNotInList(year2, number2);

		// Cambiamos al modulo correspondiente de Papelera
		changeModule(model + "Trash");

		// Verificamos que solamente hay una pagina de registros, esto es, menos
		// de 10 lineas
		assertListOnlyOnePage();

		// Almacenamos el número de lineas que hay
		int initialTrashRowCount = getListRowCount();

		// Verificamos que las dos lineas borradas anterioemente están en la
		// papelera
		assertDocumentInList(year1, number1);
		assertDocumentInList(year2, number2);

		// Restauramos usando una acción de fila
		int row1 = getDocumentRowInList(year1, number1);
		execute("Trash.restore", "row=" + row1);

		// Comprobamos que la lista de papelera tiene un registro menos
		assertListRowCount(initialTrashRowCount - 1);

		// Verificamos que el registro restaurado no se encuentra en la lista
		assertDocumentNotInList(year1, number1);

		// Recuperamos la posición del segundo registro borrado
		int row2 = getDocumentRowInList(year2, number2);

		// Lo marcamos
		checkRow(row2);

		// Restaurar seleccionando una fila y usando el botón de abajo
		execute("Trash.restore");

		// Comprobamos que la lista tiene dos registros menos
		assertListRowCount(initialTrashRowCount - 2);

		// Verificamos que el registro recién restaurado no se encuentra en la
		// lista de la papelera
		assertDocumentNotInList(year2, number2);

		// Cambiamos al módulo original
		changeModule(model);

		// Comprobamos que el número de lineas es correcto tras la restauración
		// de los registros
		assertListRowCount(initialRowCount);

		// Verificamos que los registros restaurados están en la lista
		assertDocumentInList(year1, number1);
		assertDocumentInList(year2, number2);
	}

	/**
	 * Función que nos permite verificar que el importe y el beneficio estimado
	 * está bien calculado
	 */
	private void verifyAmountAndEstimatedProfit() throws Exception {

		// Cambiamos al modo de lista
		execute("Mode.list");

		// Ponemos como filtro el año actual y el número de documento que
		// tenemos en memoria, correspondiente al documento que acabamos de
		// crear
		setConditionValues(new String[] { getCurrentYear(), getNumber() });

		// Filtramos
		execute("List.filter");

		// Comprobamos que el año concuerda
		assertValueInList(0, 0, getCurrentYear());

		// Comprobamos que el número concuerda
		assertValueInList(0, 1, getNumber());

		// Confirmamos el importe
		assertValueInList(0, "amount", "59,00");

		// Confirmamos el beneficio estimado
		assertValueInList(0, "estimatedProfit", "5,90");

		// Volvemos al modo de detalle
		execute("Mode.detailAndFirst"); // Va a modo detalle
	}

	/**
	 * Función que sirve para que verificamos los valores por defecto de una
	 * nueva factura
	 * 
	 * @throws Exception
	 */
	private void verifyDefaultValues() throws Exception {

		// Pulsamos el botón de nueva factura
		execute("CRUD.new");

		// Comprobamos que el año que aparece en el cuadro es el año actual
		assertValue("year", getCurrentYear());

		// Verificamos que el número de factura está vacio
		assertValue("number", "");

		// Verificamos que la fecha es la correcta
		assertValue("date", getCurrentDate());

		// Verificamos que el porcentaje de impuestos vale lo estipulado en el
		// fichero de configuración
		assertValue("vatPercentage", "18");

	}

	/**
	 * Función que nos permite seleccionar un cliente específico
	 * 
	 * @throws Exception
	 */
	private void chooseCustomer() throws Exception {

		// Asignamos un numero como valor para el número
		setValue("customer.number", "1");

		// Comprobamos que el nombre del cliente se corresponde con el
		// seleccionado
		assertValue("customer.name", "Rudy Mentario");

	}

	/**
	 * Función que nos permite añadir lineas de detalle a la factura
	 * 
	 * @throws Exception
	 */
	private void addDetails() throws Exception {

		// Comprobamos que no haya ninguna linea de detalle al crear una nueva
		// factura
		assertCollectionRowCount("details", 0);

		// Pulsa en el botón para añadir un nuevo elemento
		execute("Collection.new", "viewObject=xava_view_section0_details"); // viewObject
																			// es
																			// necesario
																			// para
																			// determinar
																			// a
																			// que
																			// colección
																			// nos
																			// referimos

		// Asignamos un valor al número de producto
		setValue("product.number", "1");

		// Comprobamos que el producto es el que debería
		assertValue("product.description", "Gamusino");

		// Comprobamos que el precio por unidad es el que le corresponde
		assertValue("pricePerUnit", "15,00");

		// Asignamos una cantidad para la linea de detalle
		setValue("quantity", "2");

		// Verificamos que el importe se calcula bien
		assertValue("amount", "30,00");

		// Grabamos el elemento de la colección cerrando el diálogo
		execute("Collection.save");

		// Verificamos si hay errores al grabar el detalle
		assertNoErrors();

		// Verificamos que la linea de detalle se ha introducido bien
		assertCollectionRowCount("details", 1);

		// Al grabar el primer detalle se graba el document, entonces
		// verificamos que el número del documento se ha calculado de forma
		// correcta
		assertValue("number", getNumber());

		// Vericicamos las propiedades calculadas
		assertValue("baseAmount", "30,00");
		assertValue("vat", "5,40");
		assertValue("totalAmount", "35,40");

		// Pulsa en el botón para añadir un nuevo elemento
		execute("Collection.new", "viewObject=xava_view_section0_details"); // viewObject
																			// es
																			// necesario
																			// para
																			// determinar
																			// a
																			// que
																			// colección
																			// nos
																			// referimos

		// Añadimos otro detalle
		setValue("product.number", "2");

		// Comprobamos que el producto es el que debería
		assertValue("product.description", "Gatete Motorista");

		// Comprobamos que el precio por unidad es el que le corresponde
		assertValue("pricePerUnit", "20,00");

		// Asignamos una cantidad para la linea de detalle
		setValue("quantity", "1");

		// Verificamos que el importe se calcula bien
		assertValue("amount", "20,00");

		// Grabamos el elemento de la colección cerrando el diálogo
		execute("Collection.save");

		// Verificamos si hay errores al grabar el detalle
		assertNoErrors();

		// Verificamos que la linea de detalle se ha introducido bien
		assertCollectionRowCount("details", 2);

		// Vericicamos las propiedades calculadas
		assertValue("baseAmount", "50,00");
		assertValue("vat", "9,00");
		assertValue("totalAmount", "59,00");

	}

	/**
	 * Función que nos permite añadir otras propiedades a la factura
	 * 
	 * @throws Exception
	 */
	private void setOtherProperties() throws Exception {

		// Ponemos un texto en la casilla de observaciones
		setValue("remarks", "Esto es una prueba JUnit");
	}

	/**
	 * Función que nos permite guardar una factura en la base de datos
	 * 
	 * @throws Exception
	 */
	private void save() throws Exception {

		// Pulsamos el botón de grabar factura
		execute("CRUD.save");

		// Verificamos que no se hayan producido errores
		assertNoErrors();

		// Comprobamos que se ha limpiado la casilla de número tras grabar la
		// factura
		assertValue("customer.number", "");

		// Comprobamos que se ha limpiado la lista de lineas de detalle tras
		// grabar la factura
		assertCollectionRowCount("details", 0);

		// Comprobamos que se ha limpiado la casilla de observaciones tras
		// grabar la factura
		assertValue("remarks", "");

	}

	/**
	 * Función que nos permite verificar si se ha creado correctamente una
	 * factura
	 * 
	 * @throws Exception
	 */
	private void verifyCreated() throws Exception {

		// Comprobamos que los valores que hemos recuperado de la factura son
		// los correctos
		assertValue("year", getCurrentYear());
		assertValue("number", getNumber());
		assertValue("date", getCurrentDate());
		assertValue("customer.number", "1");
		assertValue("customer.name", "Rudy Mentario");

		assertCollectionRowCount("details", 2);

		// Fila 0
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "Gamusino");
		assertValueInCollection("details", 0, "quantity", "2");
		// Fila 1
		assertValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "product.description",
				"Gatete Motorista");
		assertValueInCollection("details", 1, "quantity", "1");

		assertValue("remarks", "Esto es una prueba JUnit");
	}

	/**
	 * Función que nos permite eliminar una factura de la base de datos
	 * 
	 * @throws Exception
	 */
	private void remove() throws Exception {

		// Pulsamos el botón de eliminar
		execute("Invoicing.delete");

		// Comprobamos que no se han producido errores
		assertNoErrors();
	}

	/**
	 * Función que nos permite recuperar el año actual
	 * 
	 * @return String el año actual en formato yyyy
	 */
	private String getCurrentYear() {

		// Retornamos la fecha actual tras cambiarle el formato a yyyy
		return new SimpleDateFormat("yyyy").format(new Date());
	}

	/**
	 * Función que nos devuelve la fecha actual
	 * 
	 * @return String La fecha en formato corto
	 */
	private String getCurrentDate() {
		// Devolvemos la fecha actual tras cambiarle el formato a formato
		// dd/MM/yyyy
		return new SimpleDateFormat("dd/MM/yyyy").format(new Date());

	}

	/**
	 * Función para calcular el número que tendrá el documento comercial en la
	 * base de datos
	 */
	private void calculateNumber() {

		// Realizamos una consulta a la base de datos para recuperar el número
		// más alto del documento cuyo modelo se especifique en la prueba
		Query query = getManager().createQuery(
				"select max(i.number) from " + model
						+ " i where i.year = :year");

		// Pasamos el año actual como parámetro
		query.setParameter("year", Dates.getYear(new Date()));

		// Recuperamos el último número
		Integer lastNumber = (Integer) query.getSingleResult();

		// Si no hay último número inicializamos la variable a 0
		if (lastNumber == null) {
			lastNumber = 0;
		}

		// El número es el resultado del último número recuperado de la base de
		// datos + 1
		number = Integer.toString(lastNumber + 1);
	}

	/**
	 * Función que nos devuelve el número de un nuevo documento
	 * 
	 * @return String El número para el nuevo documento
	 */
	private String getNumber() {
		return number;
	}

	/**
	 * Función que sirve para validar que la lista de documentos comerciales
	 * tenga menos de 10 registros
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	private void assertListOnlyOnePage() throws Exception {
		assertListNotEmpty(); // De ModuleTestBase

		assertTrue("Debe haber menos de 10 lineas para ejecutar esta prueba",
				getListRowCount() < 10);
	}

	/**
	 * Función que nos permite verificar si un registro no se encuentra en el
	 * listado de documentos
	 * 
	 * @param year
	 *            Año del registro a verificar
	 * @param number
	 *            Número del registro a verificar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	private void assertDocumentNotInList(String year, String number)
			throws Exception {
		assertTrue("El documento " + year + "/" + number
				+ " no debe estar en la lista",
				getDocumentRowInList(year, number) < 0);
	}

	/**
	 * Función que nos permite verificar si un registro se encuentra en el
	 * listado de documentos
	 * 
	 * @param year
	 *            Año del registro a verificar
	 * @param number
	 *            Número del registro a verificar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	protected void assertDocumentInList(String year, String number)
			throws Exception {
		assertTrue("El documento " + year + "/" + number
				+ " debe estar en la lista",
				getDocumentRowInList(year, number) >= 0);
	}

	/**
	 * Función que nos permite localizar un registro en el listado y devolver su
	 * posición
	 * 
	 * @param year
	 *            Año del registro a verificar
	 * @param number
	 *            Número del registro a verificar
	 * @return La posición en el listado
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	protected int getDocumentRowInList(String year, String number)
			throws Exception {

		// Almacenamos la cantidad de filas que tiene el listado
		int c = getListRowCount();

		// Iteramos por las filas
		for (int i = 0; i < c; i++) {
			// Comprobamos si el año y el número de la fila actual coinciden con
			// los que estamos buscando
			if (year.equals(getValueInList(i, 0))
					&& number.equals(getValueInList(i, 1))) {
				// Si es así, devolvermos el valor de la iteración como
				// resultado
				return i;
			}
		}

		// Si no se ha encontrado el registro en la lista, devolvermos -1
		return -1;
	}

	/**
	 * Función que nos permite verificar si un cliente está en una lista
	 * 
	 * @param customerNumber
	 *            El número de cliente a comprobar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepción
	 */
	protected void assertCustomerInList(String customerNumber) throws Exception {

		// Llamamos a una función específica para realizar la validación
		assertValueForAllRows(3, customerNumber);

	}

	/**
	 * Función que nos permite verificar si todas las lineas de una lista tienen
	 * el mismo valor para una columna específica
	 * 
	 * @param column
	 *            Posición de la columna cuyo valor se quiere verificar
	 * @param value
	 *            El valor que hay que verificar
	 * @throws Exception
	 *             Si se produce algún error se lanza una excepción
	 */
	protected void assertValueForAllRows(int column, String value)
			throws Exception {

		// Nos aseguramos de que lista no está vacía
		assertListNotEmpty();

		// Almacenamos la cantidad de filas que tiene la lista
		int c = getListRowCount();

		// Iteramos por todas las filas
		for (int i = 0; i < c; i++) {

			// Verificamos que todas las filas tienen el mismo valor para la
			// columna especificada como parámetro
			if (!value.equals(getValueInList(i, column))) {
				// Si el valor no es el esperado se produce un error
				fail("La columna " + column + " de la fila " + i
						+ "no contiene el valor" + value);
			}
		}
	}

}
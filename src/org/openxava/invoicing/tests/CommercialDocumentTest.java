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

	// Para almacenar el n�mero del documento que probamos
	private String number;

	// Para almacenar el modelo de documento comercial sobre el que se est�
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
	 * Funci�n de prueba para la creaci�n de documentos
	 * 
	 * @throws Exception
	 */
	public void testCreate() throws Exception {

		// Calculamos inicialmente el n�mero que tendr� el documento en la base
		// de datos para verificar que se calcula correctamente a posteriori
		calculateNumber();

		// Verificamos los valores por defecto de una nueva factura
		verifyDefaultValues();

		// Seleccionamos un cliente
		chooseCustomer();

		// A�adimos los detalles
		addDetails();

		// Asignamos otras propiedades
		setOtherProperties();

		// Grabamos la factura
		save();

		// Prueba el m�todo de retrollamda y @formula
		verifyAmountAndEstimatedProfit();

		// Verificamos que se ha creado correctamente
		verifyCreated();

		// Eliminamos la factura
		remove();
	}

	/**
	 * Funci�n que nos permite verificar que la papelera de pedidos y de
	 * facturas funciona correctametne
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	public void testTrash() throws Exception {

		// Verificamos que solamente hay una pagina en la lista, esto es, que
		// hay menos de 10 registros
		assertListOnlyOnePage();

		// Almacenamos en n�mero de registros que hay
		int initialRowCount = getListRowCount();

		// Almacenamos el a�o y el n�mero del primer registro en la lista
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

		// Verificamos que la entidad borrada no est� en la lista
		assertDocumentNotInList(year1, number1);

		// Almacenamos el a�o y el n�mero del siguiente registro en la lista
		String year2 = getValueInList(0, 0);
		String number2 = getValueInList(0, 1);

		// Marcamos la primera fila de la lista
		checkRow(0);

		// La borramos
		execute("Invoicing.deleteSelected");

		// Verificamos que hay dos filas menos en la lista
		assertListRowCount(initialRowCount - 2);

		// Comprobamos que el registro que acabamos de borrar no est� en la
		// lista
		assertDocumentNotInList(year2, number2);

		// Cambiamos al modulo correspondiente de Papelera
		changeModule(model + "Trash");

		// Verificamos que solamente hay una pagina de registros, esto es, menos
		// de 10 lineas
		assertListOnlyOnePage();

		// Almacenamos el n�mero de lineas que hay
		int initialTrashRowCount = getListRowCount();

		// Verificamos que las dos lineas borradas anterioemente est�n en la
		// papelera
		assertDocumentInList(year1, number1);
		assertDocumentInList(year2, number2);

		// Restauramos usando una acci�n de fila
		int row1 = getDocumentRowInList(year1, number1);
		execute("Trash.restore", "row=" + row1);

		// Comprobamos que la lista de papelera tiene un registro menos
		assertListRowCount(initialTrashRowCount - 1);

		// Verificamos que el registro restaurado no se encuentra en la lista
		assertDocumentNotInList(year1, number1);

		// Recuperamos la posici�n del segundo registro borrado
		int row2 = getDocumentRowInList(year2, number2);

		// Lo marcamos
		checkRow(row2);

		// Restaurar seleccionando una fila y usando el bot�n de abajo
		execute("Trash.restore");

		// Comprobamos que la lista tiene dos registros menos
		assertListRowCount(initialTrashRowCount - 2);

		// Verificamos que el registro reci�n restaurado no se encuentra en la
		// lista de la papelera
		assertDocumentNotInList(year2, number2);

		// Cambiamos al m�dulo original
		changeModule(model);

		// Comprobamos que el n�mero de lineas es correcto tras la restauraci�n
		// de los registros
		assertListRowCount(initialRowCount);

		// Verificamos que los registros restaurados est�n en la lista
		assertDocumentInList(year1, number1);
		assertDocumentInList(year2, number2);
	}

	/**
	 * Funci�n que nos permite verificar que el importe y el beneficio estimado
	 * est� bien calculado
	 */
	private void verifyAmountAndEstimatedProfit() throws Exception {

		// Cambiamos al modo de lista
		execute("Mode.list");

		// Ponemos como filtro el a�o actual y el n�mero de documento que
		// tenemos en memoria, correspondiente al documento que acabamos de
		// crear
		setConditionValues(new String[] { getCurrentYear(), getNumber() });

		// Filtramos
		execute("List.filter");

		// Comprobamos que el a�o concuerda
		assertValueInList(0, 0, getCurrentYear());

		// Comprobamos que el n�mero concuerda
		assertValueInList(0, 1, getNumber());

		// Confirmamos el importe
		assertValueInList(0, "amount", "59,00");

		// Confirmamos el beneficio estimado
		assertValueInList(0, "estimatedProfit", "5,90");

		// Volvemos al modo de detalle
		execute("Mode.detailAndFirst"); // Va a modo detalle
	}

	/**
	 * Funci�n que sirve para que verificamos los valores por defecto de una
	 * nueva factura
	 * 
	 * @throws Exception
	 */
	private void verifyDefaultValues() throws Exception {

		// Pulsamos el bot�n de nueva factura
		execute("CRUD.new");

		// Comprobamos que el a�o que aparece en el cuadro es el a�o actual
		assertValue("year", getCurrentYear());

		// Verificamos que el n�mero de factura est� vacio
		assertValue("number", "");

		// Verificamos que la fecha es la correcta
		assertValue("date", getCurrentDate());

		// Verificamos que el porcentaje de impuestos vale lo estipulado en el
		// fichero de configuraci�n
		assertValue("vatPercentage", "18");

	}

	/**
	 * Funci�n que nos permite seleccionar un cliente espec�fico
	 * 
	 * @throws Exception
	 */
	private void chooseCustomer() throws Exception {

		// Asignamos un numero como valor para el n�mero
		setValue("customer.number", "1");

		// Comprobamos que el nombre del cliente se corresponde con el
		// seleccionado
		assertValue("customer.name", "Rudy Mentario");

	}

	/**
	 * Funci�n que nos permite a�adir lineas de detalle a la factura
	 * 
	 * @throws Exception
	 */
	private void addDetails() throws Exception {

		// Comprobamos que no haya ninguna linea de detalle al crear una nueva
		// factura
		assertCollectionRowCount("details", 0);

		// Pulsa en el bot�n para a�adir un nuevo elemento
		execute("Collection.new", "viewObject=xava_view_section0_details"); // viewObject
																			// es
																			// necesario
																			// para
																			// determinar
																			// a
																			// que
																			// colecci�n
																			// nos
																			// referimos

		// Asignamos un valor al n�mero de producto
		setValue("product.number", "1");

		// Comprobamos que el producto es el que deber�a
		assertValue("product.description", "Gamusino");

		// Comprobamos que el precio por unidad es el que le corresponde
		assertValue("pricePerUnit", "15,00");

		// Asignamos una cantidad para la linea de detalle
		setValue("quantity", "2");

		// Verificamos que el importe se calcula bien
		assertValue("amount", "30,00");

		// Grabamos el elemento de la colecci�n cerrando el di�logo
		execute("Collection.save");

		// Verificamos si hay errores al grabar el detalle
		assertNoErrors();

		// Verificamos que la linea de detalle se ha introducido bien
		assertCollectionRowCount("details", 1);

		// Al grabar el primer detalle se graba el document, entonces
		// verificamos que el n�mero del documento se ha calculado de forma
		// correcta
		assertValue("number", getNumber());

		// Vericicamos las propiedades calculadas
		assertValue("baseAmount", "30,00");
		assertValue("vat", "5,40");
		assertValue("totalAmount", "35,40");

		// Pulsa en el bot�n para a�adir un nuevo elemento
		execute("Collection.new", "viewObject=xava_view_section0_details"); // viewObject
																			// es
																			// necesario
																			// para
																			// determinar
																			// a
																			// que
																			// colecci�n
																			// nos
																			// referimos

		// A�adimos otro detalle
		setValue("product.number", "2");

		// Comprobamos que el producto es el que deber�a
		assertValue("product.description", "Gatete Motorista");

		// Comprobamos que el precio por unidad es el que le corresponde
		assertValue("pricePerUnit", "20,00");

		// Asignamos una cantidad para la linea de detalle
		setValue("quantity", "1");

		// Verificamos que el importe se calcula bien
		assertValue("amount", "20,00");

		// Grabamos el elemento de la colecci�n cerrando el di�logo
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
	 * Funci�n que nos permite a�adir otras propiedades a la factura
	 * 
	 * @throws Exception
	 */
	private void setOtherProperties() throws Exception {

		// Ponemos un texto en la casilla de observaciones
		setValue("remarks", "Esto es una prueba JUnit");
	}

	/**
	 * Funci�n que nos permite guardar una factura en la base de datos
	 * 
	 * @throws Exception
	 */
	private void save() throws Exception {

		// Pulsamos el bot�n de grabar factura
		execute("CRUD.save");

		// Verificamos que no se hayan producido errores
		assertNoErrors();

		// Comprobamos que se ha limpiado la casilla de n�mero tras grabar la
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
	 * Funci�n que nos permite verificar si se ha creado correctamente una
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
	 * Funci�n que nos permite eliminar una factura de la base de datos
	 * 
	 * @throws Exception
	 */
	private void remove() throws Exception {

		// Pulsamos el bot�n de eliminar
		execute("Invoicing.delete");

		// Comprobamos que no se han producido errores
		assertNoErrors();
	}

	/**
	 * Funci�n que nos permite recuperar el a�o actual
	 * 
	 * @return String el a�o actual en formato yyyy
	 */
	private String getCurrentYear() {

		// Retornamos la fecha actual tras cambiarle el formato a yyyy
		return new SimpleDateFormat("yyyy").format(new Date());
	}

	/**
	 * Funci�n que nos devuelve la fecha actual
	 * 
	 * @return String La fecha en formato corto
	 */
	private String getCurrentDate() {
		// Devolvemos la fecha actual tras cambiarle el formato a formato
		// dd/MM/yyyy
		return new SimpleDateFormat("dd/MM/yyyy").format(new Date());

	}

	/**
	 * Funci�n para calcular el n�mero que tendr� el documento comercial en la
	 * base de datos
	 */
	private void calculateNumber() {

		// Realizamos una consulta a la base de datos para recuperar el n�mero
		// m�s alto del documento cuyo modelo se especifique en la prueba
		Query query = getManager().createQuery(
				"select max(i.number) from " + model
						+ " i where i.year = :year");

		// Pasamos el a�o actual como par�metro
		query.setParameter("year", Dates.getYear(new Date()));

		// Recuperamos el �ltimo n�mero
		Integer lastNumber = (Integer) query.getSingleResult();

		// Si no hay �ltimo n�mero inicializamos la variable a 0
		if (lastNumber == null) {
			lastNumber = 0;
		}

		// El n�mero es el resultado del �ltimo n�mero recuperado de la base de
		// datos + 1
		number = Integer.toString(lastNumber + 1);
	}

	/**
	 * Funci�n que nos devuelve el n�mero de un nuevo documento
	 * 
	 * @return String El n�mero para el nuevo documento
	 */
	private String getNumber() {
		return number;
	}

	/**
	 * Funci�n que sirve para validar que la lista de documentos comerciales
	 * tenga menos de 10 registros
	 * 
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	private void assertListOnlyOnePage() throws Exception {
		assertListNotEmpty(); // De ModuleTestBase

		assertTrue("Debe haber menos de 10 lineas para ejecutar esta prueba",
				getListRowCount() < 10);
	}

	/**
	 * Funci�n que nos permite verificar si un registro no se encuentra en el
	 * listado de documentos
	 * 
	 * @param year
	 *            A�o del registro a verificar
	 * @param number
	 *            N�mero del registro a verificar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	private void assertDocumentNotInList(String year, String number)
			throws Exception {
		assertTrue("El documento " + year + "/" + number
				+ " no debe estar en la lista",
				getDocumentRowInList(year, number) < 0);
	}

	/**
	 * Funci�n que nos permite verificar si un registro se encuentra en el
	 * listado de documentos
	 * 
	 * @param year
	 *            A�o del registro a verificar
	 * @param number
	 *            N�mero del registro a verificar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	protected void assertDocumentInList(String year, String number)
			throws Exception {
		assertTrue("El documento " + year + "/" + number
				+ " debe estar en la lista",
				getDocumentRowInList(year, number) >= 0);
	}

	/**
	 * Funci�n que nos permite localizar un registro en el listado y devolver su
	 * posici�n
	 * 
	 * @param year
	 *            A�o del registro a verificar
	 * @param number
	 *            N�mero del registro a verificar
	 * @return La posici�n en el listado
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	protected int getDocumentRowInList(String year, String number)
			throws Exception {

		// Almacenamos la cantidad de filas que tiene el listado
		int c = getListRowCount();

		// Iteramos por las filas
		for (int i = 0; i < c; i++) {
			// Comprobamos si el a�o y el n�mero de la fila actual coinciden con
			// los que estamos buscando
			if (year.equals(getValueInList(i, 0))
					&& number.equals(getValueInList(i, 1))) {
				// Si es as�, devolvermos el valor de la iteraci�n como
				// resultado
				return i;
			}
		}

		// Si no se ha encontrado el registro en la lista, devolvermos -1
		return -1;
	}

	/**
	 * Funci�n que nos permite verificar si un cliente est� en una lista
	 * 
	 * @param customerNumber
	 *            El n�mero de cliente a comprobar
	 * @throws Exception
	 *             Si se produce un error se lanza una excepci�n
	 */
	protected void assertCustomerInList(String customerNumber) throws Exception {

		// Llamamos a una funci�n espec�fica para realizar la validaci�n
		assertValueForAllRows(3, customerNumber);

	}

	/**
	 * Funci�n que nos permite verificar si todas las lineas de una lista tienen
	 * el mismo valor para una columna espec�fica
	 * 
	 * @param column
	 *            Posici�n de la columna cuyo valor se quiere verificar
	 * @param value
	 *            El valor que hay que verificar
	 * @throws Exception
	 *             Si se produce alg�n error se lanza una excepci�n
	 */
	protected void assertValueForAllRows(int column, String value)
			throws Exception {

		// Nos aseguramos de que lista no est� vac�a
		assertListNotEmpty();

		// Almacenamos la cantidad de filas que tiene la lista
		int c = getListRowCount();

		// Iteramos por todas las filas
		for (int i = 0; i < c; i++) {

			// Verificamos que todas las filas tienen el mismo valor para la
			// columna especificada como par�metro
			if (!value.equals(getValueInList(i, column))) {
				// Si el valor no es el esperado se produce un error
				fail("La columna " + column + " de la fila " + i
						+ "no contiene el valor" + value);
			}
		}
	}

}
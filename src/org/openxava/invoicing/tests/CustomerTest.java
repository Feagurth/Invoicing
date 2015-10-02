package org.openxava.invoicing.tests;

import org.openxava.tests.*;

/**
 * Clase de pruebas para el objeto Customer
 * 
 * @author Informatica
 *
 */
public class CustomerTest extends ModuleTestBase {

	/**
	 * Constructor de la clase CustomerTest
	 * 
	 * @param testName
	 */
	public CustomerTest(String testName) {
		super(testName, "Invoicing", "Customer"); // Definimos el constructor
													// pasando el nombre de la
													// aplicaci�n y el nombre
													// objeto que se va a probar

	}

	/**
	 * Funci�n para comprobar la creaci�n de clientes
	 * 
	 * @throws Exception
	 */
	public void testACreate() throws Exception { // Los m�todos
													// de prueba han
													// de empezar
													// por 'test'

		execute("CRUD.new"); // Pulsa el bot�n 'New'
		setValue("number", "77"); // Teclea 77 como valor para el campo 'number'
		setValue("name", "JUNIT Customer"); // Pone valor en el campo 'name'
		setValue("address.street", "JUNIT Street"); // F�jate en la notaci�n del
													// punto

		// para acceder al miembro de la referencia
		setValue("address.zipCode", "77555"); // Etc
		setValue("address.city", "The JUNIT city"); // Etc
		setValue("address.state", "The JUNIT state"); // Etc
		execute("CRUD.save"); // Pulsa el bot�n 'Save'

		assertNoErrors(); // Verifica que la aplicaci�n no muestra errores

		assertValue("number", ""); // Verifica que el campo 'number' est� vac�o
		assertValue("name", ""); // Verifica que el campo 'name' est� vac�o
		assertValue("address.street", ""); // Etc
		assertValue("address.zipCode", ""); // Etc
		assertValue("address.city", ""); // Etc
		assertValue("address.state", ""); // Etc

	}

	/**
	 * Funci�n para comprobar la lectura de clientes
	 * 
	 * @throws Exception
	 */
	public void testBRead() throws Exception {
		// Leer
		execute("CRUD.new"); // Pulsa el bot�n 'New'
		setValue("number", "77"); // Pone 77 como valor para el campo 'number'
		execute("CRUD.refresh"); // Pulsa el bot�n 'Refresh'
		assertValue("number", "77"); // Verifica que el campo 'number' tiene un
										// 77
		assertValue("name", "JUNIT Customer"); // y 'name' tiene 'JUNIT
												// Customer'

		assertValue("address.street", "JUNIT Street"); // Etc
		assertValue("address.zipCode", "77555"); // Etc
		assertValue("address.city", "The JUNIT city"); // Etc
		assertValue("address.state", "The JUNIT state"); // Etc
	}

	/**
	 * Funci�n que comprueba la actualizaci�n de clientes
	 * 
	 * @throws Exception
	 */
	public void testCUpdate() throws Exception {
		// Actualizar

		execute("CRUD.new"); // Pulsa el bot�n 'New'
		setValue("number", "77"); // Pone 77 como valor para el campo 'number'
		execute("CRUD.refresh"); // Pulsa el bot�n 'Refresh'

		setValue("name", "JUNIT Customer MODIFIED"); // Cambia el valor del
														// campo 'name'
		execute("CRUD.save"); // Pulsa el bot�n 'Search'
		assertNoErrors(); // Verifica que la aplicaci�n no muestra errores
		assertValue("number", ""); // Verifica que el campo 'number' est� vac�o
		assertValue("name", ""); // Verifica que el campo 'name' est� vac�o

		// Verifica si se ha modificado
		setValue("number", "77"); // Pone 77 como valor para el campo 'number'
		execute("CRUD.refresh"); // Pulsa en el bot�n 'Refresh'
		assertValue("number", "77"); // Verifica que el campo 'number' tiene un
										// 77
		assertValue("name", "JUNIT Customer MODIFIED"); // y 'name' tiene 'JUNIT
														// Customer MODIFIED'
	}

	/**
	 * Funci�n que comprueba el borrado de clientes
	 * 
	 * @throws Exception
	 */
	public void testDDelete() throws Exception {

		execute("CRUD.new"); // Pulsa el bot�n 'New'
		setValue("number", "77"); // Pone 77 como valor para el campo 'number'
		execute("CRUD.refresh"); // Pulsa el bot�n 'Refresh'

		// Borrar
		execute("Invoicing.delete"); // Pulsa en el bot�n 'Delete'
		assertMessage("Se ha borrado el objeto correctamente"); // Verifica que
																// el
																// mensaje
																// 'Se ha
																// borrado el
																// objeto
																// correctamente'
																// se muestra al
																// usuario
	}
}

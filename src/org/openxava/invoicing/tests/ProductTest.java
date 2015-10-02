package org.openxava.invoicing.tests;

import java.math.*;

import org.openxava.invoicing.model.*;
import org.openxava.tests.*;

import static org.openxava.jpa.XPersistence.*;

public class ProductTest extends ModuleTestBase {
	private Author author; // Declaramos la entidades a crear como miembros de
							// instancia para que estén disponibles en todos los
							// métodos de prueba y puedan ser borradas al final
							// de cada prueba
	private Category category;
	private Product product1;
	private Product product2;

	public ProductTest(String testName) {
		super(testName, "Invoicing", "Product");
	}

	protected void setUp() throws Exception { // setUp() se ejecuta siempre
												// antes de cada prueba
		createProducts(); // Crea los datos usados en las pruebas
		super.setUp(); // Es necesario porque ModuleTestBase lo usa para
						// inicializarse
	}

	protected void tearDown() throws Exception { // tearDown() se ejecuta
													// siempre después de cada
													// prueba
		super.tearDown(); // Necesario, ModuleTestBase cierra recursos aquí
		removeProducts(); // Se borran los datos usado para pruebas
	}

	public void testRemoveFromList() throws Exception {

		// Establecemos los valores para el filtrado de datos
		setConditionValues(new String[] { "", "JUNIT" });

		// Especificamos el comparador para filtrar los datos
		setConditionComparators(new String[] { "=", "contains_comparator" });

		// Pulsamos el botón de filtrar
		execute("List.filter");

		// Comprobamos que hay dos filas
		assertListRowCount(2);

		// Seleccionamos la fila 1
		checkRow(1);

		// Pulsamos el botón de borrar la fila seleccionada
		execute("Invoicing.deleteSelected");

		// Comprobamos que hay una única fila tras borrar la seleccionada
		assertListRowCount(1);
	}

	public void testChangePrice() throws Exception {

		// Pulsamos el botón de nuevo, limpiando todos los campos
		execute("CRUD.new");

		// Asignamos el número de producto 1 a la casilla correspondiente al
		// número
		setValue("number", Integer.toString(product1.getNumber())); // (1)

		// Pulsamos el botón de buscar
		execute("CRUD.refresh");

		// Verificamos el precio
		assertValue("price", "10,00");

		// Cambiamos el precio
		setValue("price", "12,00");

		// Y lo almacenamos pulsando el botón de grabar
		execute("CRUD.save");

		// Verificamos que no hay errores
		assertNoErrors();

		// Comprobamos que el precio ha quedado vacío tras grabar la información
		assertValue("price", "");

		// Volvemos a introducir el número de producto 1 en la casilla
		// correspondiente a número
		setValue("number", Integer.toString(product1.getNumber())); // (1)

		// Y pulsamos el botón de buscar
		execute("CRUD.refresh");

		// Verificamos que el precio se ha modificado
		assertValue("price", "12,00");

	}

	private void createProducts() {
		// Crear objetos Java
		author = new Author(); // Se crean objetos de Java convencionales
		author.setName("JUNIT Author");

		category = new Category();
		category.setDescription("JUNIT Category");

		product1 = new Product();
		product1.setNumber(900000001);
		product1.setDescription("JUNIT Product 1");
		product1.setAuthor(author);
		product1.setCategory(category);
		product1.setPrice(new BigDecimal("10"));
		product2 = new Product();
		product2.setNumber(900000002);
		product2.setDescription("JUNIT Product 2");
		product2.setAuthor(author);
		product2.setCategory(category);
		product2.setPrice(new BigDecimal("20"));

		// Marcar los objetos como persistentes
		getManager().persist(author); // getManager() es de XPersistence.
										// persist() marca el objeto como
										// persistente para que se grabe en la
										// base de datos
		getManager().persist(category);

		getManager().persist(product1);
		getManager().persist(product2);

		// Confirma los cambios en la base de datos
		commit(); // commit() es de XPersistence. Graba todos los objetos en la
					// base de datos y confirma la transacción

	}

	/**
	 * Función que nos permite eliminar los productos creados para las pruebas
	 * de la base de datos
	 */
	private void removeProducts() {

		// Se llama a la función desde tearDown() siendo por tanto ejecutado
		// después de cada prueba.

		// Se usa la función remove para eliminar de la base de datos los
		// objetos
		remove(product1, product2, author, category);

		// Confirma los cambios en la basae de datos
		commit();
	}

	/**
	 * Función que elimina objetos de la base de datos
	 * 
	 * @param entities
	 *            Los objetos a eliminar de la base de datos
	 */
	private void remove(Object... entities) {
		// Iteramos por todos los elementos que se hayan pasado a la función
		for (Object entity : entities) {
			// Los eliminamos de la base de datos usando remove
			getManager().remove(getManager().merge(entity));
			// Se usa merge para volver a asociar las entidades a la base de
			// datos antes de borrarlas, puesto que esta asociación se pierde al
			// realizar commit (en este caso al grabarlas)
		}
	}

	public void testISBNValidator() throws Exception {
		// Buscar product1
		execute("CRUD.new");
		setValue("number", Integer.toString(product1.getNumber()));
		execute("CRUD.refresh");
		assertValue("description", "JUNIT Product 1");
		assertValue("isbn", "");
		
		// Con un formato de ISBN incorrecto
		setValue("isbn", "1111");
		execute("CRUD.save"); // Falla por el formato (apache commons validator)
		assertError("1111 no es un valor válido para ISBN de Producto: El número ISBN no es correcto");

		// ISBN existe
		setValue("isbn", "0932633439");
		execute("CRUD.save"); // No falla
		assertNoErrors();
	}

}

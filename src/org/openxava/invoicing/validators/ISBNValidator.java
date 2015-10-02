package org.openxava.invoicing.validators;

import org.apache.commons.logging.*;
import org.hibernate.validator.*;
import org.openxava.invoicing.annotations.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

public class ISBNValidator implements Validator<ISBN> {

	// Creamos una expresión regular que valide los números ISBN
	// Patrones válidos: 1234123412 | 123412341X | ISBN 0 93028 923 4 | ISBN
	// 1-56389-668-0 | ISBN 1-56389-016-X | 123456789X | ISBN 9-87654321-2 |
	// ISBN 123 456-789X | ISBN 90-70002-34-5 | ISBN 90-70002-34-x | ISBN
	// 90-70002-34-5x | ISBN 90-700-02-34-5 | 0672317249 | 9780672317248 |
	// 0-672-31724-9 | 5555555555555 | ISBN 0-596-00681-0 | ISBN-13:
	// 978-1-4028-9462-6 | ISBN: 1284233-2-1-1 | ISBN-13: 978-1-4028-9462-6 |
	// ISBN-10: 1-4028-9462-7 | ISBN-13 978-3-642-11746-6 SomeText | ISBN
	// 978-3-642-11746-6 | ISBN-10 3-642-11746-5 SomeText | ISBN 3-642-11746-5 |
	// ISBN: 978-3-642-11746-6 | ISBN : 978-3-642-11746-6
	String pattern = "(ISBN[-]*(1[03])*[ ]*(: ){0,1})*(([0-9Xx][- ]*){13}|([0-9Xx][- ]*){10})";

	private static Log log = LogFactory.getLog(ISBNValidator.class);

	// Creamos una variable para almacenar el sistema de búsqueda del ISBN
	private boolean search;

	public void initialize(ISBN isbn) {
	}

	/**
	 * Función que sirve para validar si el número ISBN es válido
	 */
	public boolean isValid(Object value) {
		// Convertimos el isbn que se nos pasa al validador a cadena y lo
		// comparamos contra el patrón de la expresión regular, devolviendo su
		// resultado

		if (Is.empty(value)) {
			return true;
		} else {
			if (!search) {
				return value.toString().matches(pattern);
			} else {
				return isbnExists(value.toString());
			}
		}
	}

	/**
	 * Función para comprobar si existe un número ISBN mediante búsqueda web
	 * 
	 * @param isbn
	 *            El número ISBN a buscar
	 * @return True si el número es válido, False si no lo es
	 */
	private boolean isbnExists(Object isbn) {
		try {
			WebClient client = new WebClient();
			HtmlPage page = (HtmlPage) client.getPage( // Llamamos a
					"http://www.bookfinder4u.com/" + // bookdiner4u
							"IsbnSearch.aspx?isbn=" + // con una URL para buscar
							isbn + "&mode=direct"); // por ISBN

			return page.asText() // Comprueba si la página resultante contiene
					.indexOf("ISBN: " + isbn) >= 0; // el ISBN buscado

		} catch (Exception ex) {
			log.warn("Impossible to connect to bookfinder4u"
					+ "to validate the ISBN. Validation fails", ex);

			return false; // Si hay algún error asumimos que la validación ha
							// fallado
		}
	}
}

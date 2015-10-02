package org.openxava.invoicing.util;

import java.io.*;
import java.math.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Clase para lectura de configuraci�n de la aplicaci�n
 * 
 * @author Informatica
 *
 */
public class InvoicingPreferences {

	// Almacenamos el nombre del fichero de configuraci�n en una variable para
	// hacer m�s c�modo su acceso
	private final static String FILE_PROPERTIES = "invoicing.properties";

	// Creamos un log
	private static Log log = LogFactory.getLog(InvoicingPreferences.class);

	// Almacenamos la configuraci�n en un objeto Properties
	private static Properties properties;

	/**
	 * Funci�n que nos permite leer el archivo de configuraci�n y devolver la
	 * informaci�n
	 * 
	 * @return Properties La informaci�n del archivo de configuraci�n de la
	 *         aplicaci�n
	 */
	private static Properties getProperties() {

		// Comprobamos si ya tenemos la configuraci�n almacenada
		if (properties == null) {
			// De no ser as�, leemos el fichero y con la clase PropertiesReader
			// de OpenXava
			PropertiesReader reader = new PropertiesReader(
					InvoicingPreferences.class, FILE_PROPERTIES);

			try {

				// Intentamos recuperar el contenido de lo leido y volcarlo al
				// objeto properties
				properties = reader.get();
			} catch (IOException ex) {

				// Si se produce un error, dejamos constancia en el log
				log.error(XavaResources.getString("properties_file_error",
						FILE_PROPERTIES), ex);

				// Y creamos un objeto properties nuevo para devolver
				properties = new Properties();
			}
		}

		// Devolvemos la informaci�n que hemos almacenado en el objeto
		// properties
		return properties;
	}

	/**
	 * Funci�n que nos permite recuperar el porcentaje por defecto de impuestos
	 * almacenado en el fichero de configuraci�n
	 * 
	 * @return BigDecimal El porcentaje por defecto de impuestos almacenado en
	 *         el fichero de configuraci�n
	 */
	public static BigDecimal getDefaultVatPercentage() {

		// Recuperamos el valor de impuestos por defecto y lo devolvemos
		return new BigDecimal(getProperties().getProperty(
				"defaultVatPercentage"));
	}
}
package org.openxava.invoicing.util;

import java.io.*;
import java.math.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Clase para lectura de configuración de la aplicación
 * 
 * @author Informatica
 *
 */
public class InvoicingPreferences {

	// Almacenamos el nombre del fichero de configuración en una variable para
	// hacer más cómodo su acceso
	private final static String FILE_PROPERTIES = "invoicing.properties";

	// Creamos un log
	private static Log log = LogFactory.getLog(InvoicingPreferences.class);

	// Almacenamos la configuración en un objeto Properties
	private static Properties properties;

	/**
	 * Función que nos permite leer el archivo de configuración y devolver la
	 * información
	 * 
	 * @return Properties La información del archivo de configuración de la
	 *         aplicación
	 */
	private static Properties getProperties() {

		// Comprobamos si ya tenemos la configuración almacenada
		if (properties == null) {
			// De no ser así, leemos el fichero y con la clase PropertiesReader
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

		// Devolvemos la información que hemos almacenado en el objeto
		// properties
		return properties;
	}

	/**
	 * Función que nos permite recuperar el porcentaje por defecto de impuestos
	 * almacenado en el fichero de configuración
	 * 
	 * @return BigDecimal El porcentaje por defecto de impuestos almacenado en
	 *         el fichero de configuración
	 */
	public static BigDecimal getDefaultVatPercentage() {

		// Recuperamos el valor de impuestos por defecto y lo devolvemos
		return new BigDecimal(getProperties().getProperty(
				"defaultVatPercentage"));
	}
}
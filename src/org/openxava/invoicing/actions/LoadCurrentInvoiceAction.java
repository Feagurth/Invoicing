package org.openxava.invoicing.actions;

import java.util.*;
import javax.inject.*;
import org.openxava.actions.*;

/**
 * Clase que nos permite cargar una factura a partir de su identificador unico
 * Extiende de SearchByViewKeyAction para poder llenar la vista a partir de la
 * clave
 * 
 * @author Informatica
 *
 */
public class LoadCurrentInvoiceAction extends SearchByViewKeyAction {

	@SuppressWarnings("rawtypes")
	@Inject
	// Usamos Inject para poder tener el valor de esta variable compartido por
	// toda la aplicaci�n
	private Map currentInvoiceKey;

	/**
	 * Funci�n que se va ejecutar cuando se lance la acci�n
	 */
	public void execute() throws Exception {
		// Recuperamos la vista actual y le asignamos los valores de la factura
		// que tenemos guardada en sesi�n y que recuperamos gracias a la
		// notaci�n @inject
		getView().setValues(currentInvoiceKey);

		// Llamamos a la funci�n execute de la clase a partir de la que
		// extendemos (SearchByViewKeyAction) para que realice las acciones
		// propias de su m�todo execute, esto es, buscar la informaci�n de la
		// factura y cargar la vista con los datos
		super.execute();
	}
}

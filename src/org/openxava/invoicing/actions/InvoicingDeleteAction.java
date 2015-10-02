package org.openxava.invoicing.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

/**
 * Clase para definir una nueva acción de borrado
 * 
 * @author Informatica
 *
 */
public class InvoicingDeleteAction extends ViewBaseAction implements
		IChainAction {

	/**
	 * Variable para almacenar la siguiente acción que se ejecutará
	 */
	private String nextAction = null;

	/**
	 * Función que se ejecuta para realizar la acción
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute() throws Exception {

		// Verificamos si hay algún objeto seleccionado
		if (getView().getKeyValuesWithValue().isEmpty()) {

			// Si no es así, mostramos un error
			addError("no_delete_not_exists");
			return;

		}

		// Comprobamos si el objeto tiene la propiedad borrado, para así poder
		// marcarlo en la base de datos como borrado sin eliminarlo realmente
		if (!getView().getMetaModel().containsMetaProperty("deleted")) {

			// Si no contiene la propiedad deleted, especificamos que la
			// siguiente acción a realizar sea un borrado normal
			nextAction = "CRUD.delete";
			return;
		}

		// Creamos un hashmap
		Map values = new HashMap();

		// Asignamos true a la propiedad deleted y lo almacenamos en el hashmap
		values.put("deleted", true);

		// Asignamos los valores usando MapFacade
		MapFacade.setValues(getModelName(), // Un metodo de ViewBaseAction
				getView().getKeyValues(), // La clave de la entidad a modificar
				values // Los valores a cambiar
				);

		// Reiniciamos la cache para los desplegables
		resetDescriptionsCache();

		// Mostramos un mensaje al usuario
		addMessage("", getModelName());

		// Limpiamos la vista
		getView().clear();

		// Hacemos la vista como no editable
		getView().setEditable(false);

	}

	/**
	 * Función que nos permite recuperar la siguiente acción a ajecutar
	 */
	public String getNextAction() throws Exception {
		
		// Si es nulo, no se encadena ninguna acción
		return nextAction;
	}

}

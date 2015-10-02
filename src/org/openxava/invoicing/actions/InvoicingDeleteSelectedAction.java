package org.openxava.invoicing.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.validators.*;

/**
 * Clase para la eliminaci�n de registros desde el modo lista. Extendemos de
 * TabBaseAction para poder trabajar con datos tabulares por medio de getTab().
 * Implementamos las funciones de IChainAction para poder encadenar acciones.
 * 
 * @author Informatica
 *
 */
public class InvoicingDeleteSelectedAction extends TabBaseAction implements
		IChainAction {

	/**
	 * Variable para almacenar la siguiente acci�n a ejecutar
	 */
	private String nextAction = null;

	/**
	 * Variable para almacenar si el entidada ser� restaurada o no
	 */
	private boolean restore;

	/**
	 * Funci�n que ejecuta la l�gica de la clase
	 */
	public void execute() throws Exception {

		// Verificamos que la entidad contenga la propiedad deleted
		if (!getMetaModel().containsMetaProperty("deleted")) {
			// Si no es as�, la siguiente acci�n a ejecutar es
			// CRUD.deleteSelected
			nextAction = "CRUD.deleteSelected";

			return;
		}

		// Si la entidad tiene la propiedad deleted, marcamos las entidades
		// seleccionadas como borradas
		markSelectedEntitiesAsDeleted();
	}

	/**
	 * Funci�n que nos permite recuperar el modelos de metadatos de una entidad
	 * 
	 * @return MetaModel El modelo de metadatos de una entidad
	 */
	private MetaModel getMetaModel() {
		return MetaModel.get(getTab().getModelName());
	}

	/**
	 * Funci�n que nos pemirte recuperar la siguiente acci�n que se va a
	 * ejecutar
	 */
	public String getNextAction() throws Exception {
		// Si es nulo no se encadena con ninguna acci�n
		return nextAction;
	}

	/**
	 * Funci�n que nos permite marcar en la base de datos las entidades
	 * seleccionadas como eliminadas
	 * 
	 * @throws Exception
	 *             Si se produce alg�n error se lanzar� una excepci�n
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private void markSelectedEntitiesAsDeleted() throws Exception {
		// Valores a asignar a cada entidad para marcarla
		Map values = new HashMap();

		// A�adimos la propiedad deleted a con el valor inverso al de restauraci�n al mapa de valores
		values.put("deleted", !isRestore());

		// Iteramos por todas las filas seleccionadas
		for (int row : getSelected()) {

			// Recuperamos los valores de cada fila y los volcamos en un mapa de
			// datos
			Map key = (Map) getTab().getTableModel().getObjectAt(row);

			try {
				// Modificamos los valores de la fila a�adiendo la propiedad
				// deleted a los que tuviese anteriormente
				MapFacade.setValues(getTab().getModelName(), key, values);
			} catch (ValidationException ex) {
				// Si se produce una ValidationException mostramos mensajes de
				// error
				addError("no_delete_row", row + 1, key);
				addErrors(ex.getErrors()); //
			} catch (Exception ex) {
				// Si se lanza cualquier otra excepci�n, se a�ade un mensaje
				// gen�rico
				addError("no_delete_row", row + 1, key);
			}
		}

		// Tras borrar, desmarcamos las filas
		getTab().deselectAll();

		// Y reiniciamos el cach� de los combos para este usuario
		resetDescriptionsCache();
	}

	/**
	 * Funci�n que nos permite recuperar si la entidad est� restaurada
	 * 
	 * @return True si la entidad est� restaurada y False en caso contrario
	 */
	public boolean isRestore() {
		return restore;
	}

	/**
	 * Funci�n que nos permite asignar el estado de restauraci�n de una entidad
	 * 
	 * @param restore
	 *            True si queremos que la entidad sea restaurada y False en caso
	 *            contrario
	 */
	public void setRestore(boolean restore) {
		this.restore = restore;
	}

}
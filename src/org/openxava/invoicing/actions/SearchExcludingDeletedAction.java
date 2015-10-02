package org.openxava.invoicing.actions;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;

/**
 * Clase para realizar busquedas en la base de datos sin que aparezcan los
 * registros marcados con el campo deleted a verdadero
 * 
 * Heredamos de la clase SearchByViewKeyAction que es la clasae estandard de
 * OpenXava para buscar
 * 
 * @author Informatica
 *
 */
public class SearchExcludingDeletedAction extends SearchExecutingOnChangeAction {

	/**
	 * Funci�n que nos permite comprobar si una entidad tiene la propiedad
	 * deleted
	 * 
	 * @return True si la entidad es borrable, False en caso contrario
	 */
	private boolean isDeletable() {

		// Recuperamos la vista actual y comprobamos si en tu modelo cuenta con
		// la propiedad deleted
		return getView().getMetaModel().containsMetaProperty("deleted");
	}

	/**
	 * Funci�n que nos permite recuperar los valores visualizados en una vista
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map getValuesFromView() throws Exception {

		// Comprobamos si la entidad es borrable
		if (!isDeletable()) {
			// Si no es 'deletable' usamos la l�gica est�ndar
			return super.getValuesFromView();
		}

		// Recuperamos los valores de la vista y los almacenamos en un mapa
		Map values = super.getValuesFromView();

		// A�adimos el valor deleted como falso al mapa
		values.put("deleted", false);

		// Y retornamos los valores
		return values;
	}

	/**
	 * Funci�n que nos permite recuperar los nombres de los miembros a leer de
	 * la entidad
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map getMemberNames() throws Exception {

		// Comprobamos si la entidad es borrable
		if (!isDeletable()) {
			// Si no es 'deletable' usamos la l�gica est�ndar
			return super.getMemberNames();
		}

		// Recuperamos los valores de la vista y los almacenamos en un mapa
		Map members = super.getMemberNames();

		// A�adimos el valor deleted como falso al mapa
		members.put("deleted", null);

		// Y retornamos los valores
		return members;
	}

	/**
	 * Funci�n que nos permite asignar los valores desde la entidad a la vista
	 */
	@SuppressWarnings("rawtypes")
	protected void setValuesToView(Map values) throws Exception {
		// Verificamos si la entidad es borrable y si su propiedad deleted es
		// verdadera
		if (isDeletable() && (Boolean) values.get("deleted")) {
			// Si se dan las condiciones podemos decir que la entidad est�
			// borrada, para que no aparezca en los resultados de la b�squeda
			// lanzamos la misma excepci�n que lanza OpenXava cuando un objeto
			// no se encuntra
			throw new ObjectNotFoundException();
		} else {
			// Si las condiciones no se cumplen la entidad no est� realmente
			// borrada, y procedemos con la l�gica est�ndar
			super.setValuesToView(values);
		}
	}
}
package org.openxava.invoicing.actions;

import org.openxava.actions.*;

public class SaveInvoiceAction extends SaveAction implements
		IChangeModuleAction {

	/**
	 * Función que nos sirve para especificar el siguiente modulo al que se
	 * llamará
	 */
	@Override
	public String getNextModule() {
		// Devolvemos esta variable para volver al módulo anterior
		return PREVIOUS_MODULE;
	}

	/**
	 * Función que nos permite saber si el siguiente módulo debe inicializarse
	 * cada vez que cambiamos a él
	 */
	@Override
	public boolean hasReinitNextModule() {
		// Como no interesa inicializar el módulo Order devolvemos false
		return false;
	}

}

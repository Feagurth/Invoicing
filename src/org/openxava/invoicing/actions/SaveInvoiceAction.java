package org.openxava.invoicing.actions;

import org.openxava.actions.*;

public class SaveInvoiceAction extends SaveAction implements
		IChangeModuleAction {

	/**
	 * Funci�n que nos sirve para especificar el siguiente modulo al que se
	 * llamar�
	 */
	@Override
	public String getNextModule() {
		// Devolvemos esta variable para volver al m�dulo anterior
		return PREVIOUS_MODULE;
	}

	/**
	 * Funci�n que nos permite saber si el siguiente m�dulo debe inicializarse
	 * cada vez que cambiamos a �l
	 */
	@Override
	public boolean hasReinitNextModule() {
		// Como no interesa inicializar el m�dulo Order devolvemos false
		return false;
	}

}

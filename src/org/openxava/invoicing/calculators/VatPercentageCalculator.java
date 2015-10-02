package org.openxava.invoicing.calculators;

import org.openxava.calculators.*;
import org.openxava.invoicing.util.*;

public class VatPercentageCalculator implements ICalculator {

	/**
	 * Uid Generado autom�ticamente
	 */
	private static final long serialVersionUID = -7368834092152062493L;

	/**
	 * Funci�n que nos permite recuperar el porcentaje de impuestos por defecto
	 * de la configuraci�n de la aplicaci�n
	 */
	public Object calculate() throws Exception {

		return InvoicingPreferences.getDefaultVatPercentage();
	}

}

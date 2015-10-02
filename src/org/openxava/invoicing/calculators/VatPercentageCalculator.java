package org.openxava.invoicing.calculators;

import org.openxava.calculators.*;
import org.openxava.invoicing.util.*;

public class VatPercentageCalculator implements ICalculator {

	/**
	 * Uid Generado automáticamente
	 */
	private static final long serialVersionUID = -7368834092152062493L;

	/**
	 * Función que nos permite recuperar el porcentaje de impuestos por defecto
	 * de la configuración de la aplicación
	 */
	public Object calculate() throws Exception {

		return InvoicingPreferences.getDefaultVatPercentage();
	}

}

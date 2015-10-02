package org.openxava.invoicing.actions;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.invoicing.model.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Clase para realizar las acciones que suceden al realizar la acci�n de a�adir
 * pedidos a una factura
 * 
 * @author Informatica
 *
 */
public class AddOrdersToInvoiceAction extends AddElementsToCollectionAction {

	/**
	 * Funci�n que se ejecuta cuando se realiza la acci�n
	 */
	public void execute() throws Exception {

		// Ejecutamos la l�gica est�ndard de AddElementsToCollectionAction
		super.execute();

		// Refrescamos la vista para mostrar y recalcular los datos nuevos
		getView().refresh();

	}

	@SuppressWarnings("rawtypes")
	protected void associateEntity(Map keyValues) throws ValidationException,
			XavaException, ObjectNotFoundException, FinderException,
			RemoteException {

		// Ejecutamos la l�gica est�dard
		super.associateEntity(keyValues);

		// Buscamos el pedido y lo almacenamos en una variable
		Order order = (Order) MapFacade.findEntity("Order", keyValues);

		// Copiamos la informaci�n del pedido a la factura
		order.copyDetailsToInvoice();

	}

}

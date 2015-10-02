package org.openxava.invoicing.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.invoicing.calculators.*;
import org.openxava.jpa.*;

/**
 * Clase para documentos comerciales, desde donde heredar�n las clases de
 * facturas y pedidos
 * 
 * @author Informatica
 *
 */
@Entity
@View(members = "year, number, date," + "data{" + "customer;" + "details;"
		+ "amounts[vatPercentage, baseAmount, vat, totalAmount];" + "remarks"
		+ "}")
abstract public class CommercialDocument extends Deleteable {

	/**
	 * Variable para almacenar el a�o del documento
	 */
	@Column(length = 4)
	// Definimos el tama�o m�ximo que tendr� la variable
	@DefaultValueCalculator(CurrentYearCalculator.class)
	// Especificamos el sistema de calcula por defecto para la variable
	@SearchKey
	// Incluimos esta anotaci�n para hacer de la variable una clave de b�squeda
	private int year;

	/**
	 * Variable para almacenar el n�mero del documento
	 */
	@Column(length = 6)
	// Definimos el tama�o m�ximo de la variable
	@ReadOnly
	// La variable no puede ser modificada por el usuario
	@SearchKey
	// Incluimos esta anotaci�n para hacer de la variable una clave de b�squeda
	private int number;

	/**
	 * Variable para almacenar la fecha del documento
	 */
	@Required
	// Especificamos que el campo es obligatorio
	@DefaultValueCalculator(CurrentDateCalculator.class)
	// Especificamos el sistema de calcula por defecto para la variable
	private Date date;

	/**
	 * Variable para almacenar el cliente
	 */
	@ManyToOne( // Se especifica la relaci�n del cliente con el documento
	fetch = FetchType.LAZY, // Se define el tipo de carga que realizar�n los
							// clientes
	optional = false)
	// Los clientes ser�n obligatorios en los documentos
	@ReferenceView("Simple")
	// Usamos la vista reducida que hemos definido en la clase Customer
	private Customer customer;

	/**
	 * Colecci�n de objetos de la clase Detail donde se almacenar�n las lineas
	 * del documento
	 */

	@OneToMany( // Especificamos la relaci�n entre la colecci�n de detalles y el
				// documento
	mappedBy = "parent", // Especificamos donde se almacena la relaci�n entre
							// lineas y documentos, siendo en este caso
							// almacenadas en lineas

	cascade = CascadeType.ALL // Definimos la dependencia de la colecci�n de
								// detalle con el documento. En este caso se
								// borran todas las lineas que est�n
								// relacionadas con el documento que se borre.
	)
	@ListProperties("product.number, product.description, quantity, pricePerUnit, amount")
	// Definimos los valores que queremos que se muestren en la pantalla cuando
	// se recupere la informaci�n de la colecci�n de la lista
	private Collection<Detail> details = new ArrayList<Detail>();

	/**
	 * Variable para almacenar las notas referentes a los documentos
	 */
	@Stereotype("MEMO")
	// Especificamos el uso de la variable como un estereotipo de tipo memo
	private String remarks;

	/**
	 * Variable para almacenar el porcentaje de impuestos del documento
	 * comercial
	 */
	@Digits(integerDigits = 2, fractionalDigits = 0)
	// Especifica la codificaci�n del n�mero para dos cifras sin decimales
	@Required
	// Hace obligatorio la introducci�n de valores en el campo
	@DefaultValueCalculator(VatPercentageCalculator.class)
	// Usamos una clase creada a tal efecto para recupearar el porcentaje de
	// impuestos por defecto de la configuraci�n de la aplicaci�n
	private BigDecimal vatPercentage;

	/**
	 * Variable para almacenar el importe del documento comercial
	 */
	@Stereotype("MONEY")
	// Especificamos que la variable sea formateada como tipo dinero
	private BigDecimal amount;

	@org.hibernate.annotations.Formula("AMOUNT * 0.10")
	// Especificamos una formula para el calculo del beneficio estimado,
	// realizandose de este modo de forma autom�tica en la base de datos
	@Stereotype("MONEY")
	// Especificamos que la variable sea formateada como tipo dinero
	private BigDecimal estimatedProfit;

	/**
	 * Variable para verificar si JPA est� borrando datos
	 */
	@Transient
	// Tag para especificar que la variable no se va a almacenar en la base de
	// datos
	private boolean removing = false;

	/**
	 * Funci�n que devulve si el documento comercial est� siendo borrado
	 * 
	 * @return True si est� siendo borrado, False en caso contrario
	 */
	boolean isRemoving() {
		return removing;
	}

	/**
	 * Funci�n que nos permite marcar el documento comercial como siendo
	 * eliminado
	 */
	@PreRemove
	// Especificamos que se ejecute antes de la eliminaci�n del documento
	// comercial
	private void markRemoving() {
		this.removing = true;
	}

	/**
	 * Funci�n que nos permite marcar el documento comercial como no siendo
	 * eliminado
	 */
	@PostRemove
	// Especificamos que se ejecute despues de la eliminaci�n del documento
	// comercial
	private void unmarkRemoving() {
		this.removing = false;
	}

	/**
	 * Funci�n que nos permite recuperar el a�o asociada a el documento
	 * 
	 * @return int El a�o asociado a el documento
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Funci�n que nos permite asignar el a�o de el documento
	 * 
	 * @param year
	 *            El a�o de el documento
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Funci�n que nos permite recuperar el n�mero de el documento
	 * 
	 * @return int El n�mero de la factura.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Funci�n que nos permite asignar un n�mero a el documento
	 * 
	 * @param number
	 *            El n�mero que queremos asignar a el documento
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Funci�n que nos permite recuperar la fecha asociada a el documento
	 * 
	 * @return Date La fecha asociada a la factura
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Funci�n que nos permite asignar una fecha a el documento
	 * 
	 * @param date
	 *            La fecha que queremos asignar a el documento
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Funci�n que nos permite recuperar el cliente asignado a el documento
	 * 
	 * @return Customer El cliente asignado a el documento
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Funci�n que nos permite asignar un cliente a el documento
	 * 
	 * @param customer
	 *            El cliente que queremos asignar a el documento
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Funci�n que nos permite recuperar la colecci�n de detalles que forman
	 * parte de el documento
	 * 
	 * @return Collection<Detail> La colecci�n de linas de detalle
	 */
	public Collection<Detail> getDetails() {
		return details;
	}

	/**
	 * Funci�n que nos permite asingar una colecci�n de lineas de detalle a el
	 * documento
	 * 
	 * @param details
	 *            La colecci�n de linas de detalle
	 */
	public void setDetails(Collection<Detail> details) {
		this.details = details;
	}

	/**
	 * Funci�n que nos permite recuperar los comentarios asociados a el
	 * documento
	 * 
	 * @return String Los comentarios asociados a el documento
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Funci�n que nos permite asignar comentarios a el documento
	 * 
	 * @param remarks
	 *            Los comentarios que queramos asignar a el documento
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Funci�n que nos permite calcular el importe total de todas las lineas de
	 * detalle del documento comercial
	 * 
	 * @return BigDecimal El importe total de todas las lineas de detalle del
	 *         documento comercial
	 */
	@Stereotype("MONEY")
	public BigDecimal getBaseAmount() {

		// Creamos una nueva variable para almacenar los resultados
		BigDecimal result = new BigDecimal("0.00");

		// Iteramos por todas las lineas de detalle que tenga el documento
		// comercial
		for (Detail detail : getDetails()) {

			// Vamos a�adiendo a la variable de almacenaje el importe de cada
			// linea de detalle
			result = result.add(detail.getAmount());
		}

		// Devolvemos el resultado
		return result;
	}

	/**
	 * Funci�n que nos permite recuperar el porcentaje de impuestos del
	 * documento comercial
	 * 
	 * @return BigDecimal El porcentaje de impuestos del documento comercial
	 */
	public BigDecimal getVatPercentage() {
		// Devolvemos 0 si no tenemos un porcentaje
		return vatPercentage == null ? BigDecimal.ZERO : vatPercentage;
	}

	/**
	 * Funci�n que nos permite asignar un porcentaje de impuestos al documento
	 * comercial
	 * 
	 * @param vatPercentage
	 *            El porcentaje de impuestos a asignar al documento comercial
	 */
	public void setVatPercentage(BigDecimal vatPercentage) {
		this.vatPercentage = vatPercentage;
	}

	/**
	 * Funci�n que nos permite calcular el importe de los impuestos del
	 * documento comercial
	 * 
	 * @return BigDecimal El importe de los impuestos del documento comercial
	 */
	@Stereotype("MONEY")
	// Especificamos que el resultado sea tratado con la codificaci�n del dinero
	@Depends("vatPercentage")
	// Hacemos que la funci�n dependa de vatPorcentage y se ejecute cada vez que
	// este valor cambie
	public BigDecimal getVat() {

		// Devolvemos el resultado de multiplicar el importe del documento
		// comercial por el porcentaje de impuestos y finalmente dividirlo entre
		// 100
		return getBaseAmount().multiply(getVatPercentage()).divide(
				new BigDecimal("100"));

	}

	/**
	 * Funci�n que nos permtie recupear el importe total del documento comercial
	 * 
	 * @return BigDecimal El importe total del documento comercial
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Funci�n que nos permite asignar un importe total al documento comercial
	 * 
	 * @param amount
	 *            El importe total que queremos asignar al documento comercial
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Funci�n que nos sirve para calcular el coste total de un documento
	 * comercial, impuestos incluidos
	 * 
	 * @return El coste total del documento comercial, impuestos incluidos
	 */
	@Stereotype("MONEY")
	// Especificamos que el resultado sea codificado como dinero
	@Depends("baseAmount, vat")
	// Hacemos que la funci�n dependa de los valores de baseAmount y de vat para
	// que se recalculen cada vez que estos valores se modifiquen
	public BigDecimal getTotalAmount() {

		// Devolvemos el valor acumulado de las lineas de detalle m�s el coste
		// de los impuestos del valor calculado
		return getBaseAmount().add(getVat());

	}

	/**
	 * Funci�n que nos permite calcular el n�mero de documento antes de grabarlo
	 * en la base de datos
	 * 
	 * @throws Exception
	 *             Se lanza una excepci�n si se produce un error
	 */
	@PrePersist
	// Es necesario para especificar que la funci�n se ejecuta ante de grabar el
	// objeto por primera vez
	public void calculateNumber() throws Exception {

		// Creamos una query para recuperar el mayor n�mero de documento de un
		// tipo espec�fico para este a�o
		Query query = XPersistence.getManager().createQuery(
				"select max(i.number) from " + getClass().getSimpleName()
						+ " i where i.year =:year");

		// Pasamos como par�metro el a�o actual
		query.setParameter("year", year);

		// Recuperamos en n�mero y lo almacenamos en una variable
		Integer lastNumber = (Integer) query.getSingleResult();

		// Si el n�mero recuperado es nulo asignamos como valor 1, en caso
		// contrario le asignamos el valor del n�mero recuperado m�s 1
		this.number = lastNumber == null ? 1 : lastNumber + 1;

	}

	/**
	 * Funci�n que nos permite recalcular el importe de un documento comercial
	 * cuando se modifiquen de alg�n modo sus lineas comerciales, de este modo
	 * se puede almacenar en la base de datos el importe de cada documento
	 * comercial de forma persistente
	 */
	public void recalculateAmount() {

		// Calculamos el importe total y lo asignamos
		setAmount(getTotalAmount());

	}

	/**
	 * Funci�n que nos permite calcular el beneficio estimado de cada documento
	 * comercial
	 * 
	 * @return BigDecimal El beneficio estimado del documento comercial
	 */
	public BigDecimal getEstimatedProfit() {
		return estimatedProfit;
	}

	/**
	 * Permite convertir el objeto documento comercial en una cadena
	 * especificando unicamente el a�o y el n�mero del documento
	 */
	@Override
	public String toString() {
		return year + "/" + number;
	}

}

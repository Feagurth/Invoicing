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
 * Clase para documentos comerciales, desde donde heredarán las clases de
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
	 * Variable para almacenar el año del documento
	 */
	@Column(length = 4)
	// Definimos el tamaño máximo que tendrá la variable
	@DefaultValueCalculator(CurrentYearCalculator.class)
	// Especificamos el sistema de calcula por defecto para la variable
	@SearchKey
	// Incluimos esta anotación para hacer de la variable una clave de búsqueda
	private int year;

	/**
	 * Variable para almacenar el número del documento
	 */
	@Column(length = 6)
	// Definimos el tamaño máximo de la variable
	@ReadOnly
	// La variable no puede ser modificada por el usuario
	@SearchKey
	// Incluimos esta anotación para hacer de la variable una clave de búsqueda
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
	@ManyToOne( // Se especifica la relación del cliente con el documento
	fetch = FetchType.LAZY, // Se define el tipo de carga que realizarán los
							// clientes
	optional = false)
	// Los clientes serán obligatorios en los documentos
	@ReferenceView("Simple")
	// Usamos la vista reducida que hemos definido en la clase Customer
	private Customer customer;

	/**
	 * Colección de objetos de la clase Detail donde se almacenarán las lineas
	 * del documento
	 */

	@OneToMany( // Especificamos la relación entre la colección de detalles y el
				// documento
	mappedBy = "parent", // Especificamos donde se almacena la relación entre
							// lineas y documentos, siendo en este caso
							// almacenadas en lineas

	cascade = CascadeType.ALL // Definimos la dependencia de la colección de
								// detalle con el documento. En este caso se
								// borran todas las lineas que estén
								// relacionadas con el documento que se borre.
	)
	@ListProperties("product.number, product.description, quantity, pricePerUnit, amount")
	// Definimos los valores que queremos que se muestren en la pantalla cuando
	// se recupere la información de la colección de la lista
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
	// Especifica la codificación del número para dos cifras sin decimales
	@Required
	// Hace obligatorio la introducción de valores en el campo
	@DefaultValueCalculator(VatPercentageCalculator.class)
	// Usamos una clase creada a tal efecto para recupearar el porcentaje de
	// impuestos por defecto de la configuración de la aplicación
	private BigDecimal vatPercentage;

	/**
	 * Variable para almacenar el importe del documento comercial
	 */
	@Stereotype("MONEY")
	// Especificamos que la variable sea formateada como tipo dinero
	private BigDecimal amount;

	@org.hibernate.annotations.Formula("AMOUNT * 0.10")
	// Especificamos una formula para el calculo del beneficio estimado,
	// realizandose de este modo de forma automática en la base de datos
	@Stereotype("MONEY")
	// Especificamos que la variable sea formateada como tipo dinero
	private BigDecimal estimatedProfit;

	/**
	 * Variable para verificar si JPA está borrando datos
	 */
	@Transient
	// Tag para especificar que la variable no se va a almacenar en la base de
	// datos
	private boolean removing = false;

	/**
	 * Función que devulve si el documento comercial está siendo borrado
	 * 
	 * @return True si está siendo borrado, False en caso contrario
	 */
	boolean isRemoving() {
		return removing;
	}

	/**
	 * Función que nos permite marcar el documento comercial como siendo
	 * eliminado
	 */
	@PreRemove
	// Especificamos que se ejecute antes de la eliminación del documento
	// comercial
	private void markRemoving() {
		this.removing = true;
	}

	/**
	 * Función que nos permite marcar el documento comercial como no siendo
	 * eliminado
	 */
	@PostRemove
	// Especificamos que se ejecute despues de la eliminación del documento
	// comercial
	private void unmarkRemoving() {
		this.removing = false;
	}

	/**
	 * Función que nos permite recuperar el año asociada a el documento
	 * 
	 * @return int El año asociado a el documento
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Función que nos permite asignar el año de el documento
	 * 
	 * @param year
	 *            El año de el documento
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Función que nos permite recuperar el número de el documento
	 * 
	 * @return int El número de la factura.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Función que nos permite asignar un número a el documento
	 * 
	 * @param number
	 *            El número que queremos asignar a el documento
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Función que nos permite recuperar la fecha asociada a el documento
	 * 
	 * @return Date La fecha asociada a la factura
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Función que nos permite asignar una fecha a el documento
	 * 
	 * @param date
	 *            La fecha que queremos asignar a el documento
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Función que nos permite recuperar el cliente asignado a el documento
	 * 
	 * @return Customer El cliente asignado a el documento
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Función que nos permite asignar un cliente a el documento
	 * 
	 * @param customer
	 *            El cliente que queremos asignar a el documento
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Función que nos permite recuperar la colección de detalles que forman
	 * parte de el documento
	 * 
	 * @return Collection<Detail> La colección de linas de detalle
	 */
	public Collection<Detail> getDetails() {
		return details;
	}

	/**
	 * Función que nos permite asingar una colección de lineas de detalle a el
	 * documento
	 * 
	 * @param details
	 *            La colección de linas de detalle
	 */
	public void setDetails(Collection<Detail> details) {
		this.details = details;
	}

	/**
	 * Función que nos permite recuperar los comentarios asociados a el
	 * documento
	 * 
	 * @return String Los comentarios asociados a el documento
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Función que nos permite asignar comentarios a el documento
	 * 
	 * @param remarks
	 *            Los comentarios que queramos asignar a el documento
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Función que nos permite calcular el importe total de todas las lineas de
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

			// Vamos añadiendo a la variable de almacenaje el importe de cada
			// linea de detalle
			result = result.add(detail.getAmount());
		}

		// Devolvemos el resultado
		return result;
	}

	/**
	 * Función que nos permite recuperar el porcentaje de impuestos del
	 * documento comercial
	 * 
	 * @return BigDecimal El porcentaje de impuestos del documento comercial
	 */
	public BigDecimal getVatPercentage() {
		// Devolvemos 0 si no tenemos un porcentaje
		return vatPercentage == null ? BigDecimal.ZERO : vatPercentage;
	}

	/**
	 * Función que nos permite asignar un porcentaje de impuestos al documento
	 * comercial
	 * 
	 * @param vatPercentage
	 *            El porcentaje de impuestos a asignar al documento comercial
	 */
	public void setVatPercentage(BigDecimal vatPercentage) {
		this.vatPercentage = vatPercentage;
	}

	/**
	 * Función que nos permite calcular el importe de los impuestos del
	 * documento comercial
	 * 
	 * @return BigDecimal El importe de los impuestos del documento comercial
	 */
	@Stereotype("MONEY")
	// Especificamos que el resultado sea tratado con la codificación del dinero
	@Depends("vatPercentage")
	// Hacemos que la función dependa de vatPorcentage y se ejecute cada vez que
	// este valor cambie
	public BigDecimal getVat() {

		// Devolvemos el resultado de multiplicar el importe del documento
		// comercial por el porcentaje de impuestos y finalmente dividirlo entre
		// 100
		return getBaseAmount().multiply(getVatPercentage()).divide(
				new BigDecimal("100"));

	}

	/**
	 * Función que nos permtie recupear el importe total del documento comercial
	 * 
	 * @return BigDecimal El importe total del documento comercial
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Función que nos permite asignar un importe total al documento comercial
	 * 
	 * @param amount
	 *            El importe total que queremos asignar al documento comercial
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Función que nos sirve para calcular el coste total de un documento
	 * comercial, impuestos incluidos
	 * 
	 * @return El coste total del documento comercial, impuestos incluidos
	 */
	@Stereotype("MONEY")
	// Especificamos que el resultado sea codificado como dinero
	@Depends("baseAmount, vat")
	// Hacemos que la función dependa de los valores de baseAmount y de vat para
	// que se recalculen cada vez que estos valores se modifiquen
	public BigDecimal getTotalAmount() {

		// Devolvemos el valor acumulado de las lineas de detalle más el coste
		// de los impuestos del valor calculado
		return getBaseAmount().add(getVat());

	}

	/**
	 * Función que nos permite calcular el número de documento antes de grabarlo
	 * en la base de datos
	 * 
	 * @throws Exception
	 *             Se lanza una excepción si se produce un error
	 */
	@PrePersist
	// Es necesario para especificar que la función se ejecuta ante de grabar el
	// objeto por primera vez
	public void calculateNumber() throws Exception {

		// Creamos una query para recuperar el mayor número de documento de un
		// tipo específico para este año
		Query query = XPersistence.getManager().createQuery(
				"select max(i.number) from " + getClass().getSimpleName()
						+ " i where i.year =:year");

		// Pasamos como parámetro el año actual
		query.setParameter("year", year);

		// Recuperamos en número y lo almacenamos en una variable
		Integer lastNumber = (Integer) query.getSingleResult();

		// Si el número recuperado es nulo asignamos como valor 1, en caso
		// contrario le asignamos el valor del número recuperado más 1
		this.number = lastNumber == null ? 1 : lastNumber + 1;

	}

	/**
	 * Función que nos permite recalcular el importe de un documento comercial
	 * cuando se modifiquen de algún modo sus lineas comerciales, de este modo
	 * se puede almacenar en la base de datos el importe de cada documento
	 * comercial de forma persistente
	 */
	public void recalculateAmount() {

		// Calculamos el importe total y lo asignamos
		setAmount(getTotalAmount());

	}

	/**
	 * Función que nos permite calcular el beneficio estimado de cada documento
	 * comercial
	 * 
	 * @return BigDecimal El beneficio estimado del documento comercial
	 */
	public BigDecimal getEstimatedProfit() {
		return estimatedProfit;
	}

	/**
	 * Permite convertir el objeto documento comercial en una cadena
	 * especificando unicamente el año y el número del documento
	 */
	@Override
	public String toString() {
		return year + "/" + number;
	}

}

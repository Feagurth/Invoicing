package org.openxava.invoicing.annotations;

import java.lang.annotation.*;
import org.hibernate.validator.*;
import org.openxava.invoicing.validators.*;

@ValidatorClass(ISBNValidator.class)
// Esta clase contiene la l�gica de validaci�n
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
// Una definici�n de anotaci�n Java convencional
public @interface ISBN {
	// Para conmutar la b�squeda por web del ISBN
	boolean search() default true; 
	// Mensaje por si la validaci�n falla
	String message() default "El n�mero ISBN no es correcto";
	
}
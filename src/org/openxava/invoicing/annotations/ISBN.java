package org.openxava.invoicing.annotations;

import java.lang.annotation.*;
import org.hibernate.validator.*;
import org.openxava.invoicing.validators.*;

@ValidatorClass(ISBNValidator.class)
// Esta clase contiene la lógica de validación
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
// Una definición de anotación Java convencional
public @interface ISBN {
	// Para conmutar la búsqueda por web del ISBN
	boolean search() default true; 
	// Mensaje por si la validación falla
	String message() default "El número ISBN no es correcto";
	
}
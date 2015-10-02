package org.openxava.invoicing.actions;

import java.io.*;

import it.sauronsoftware.ftp4j.*;

import org.openxava.actions.*;

public class UploadFile extends ViewBaseAction {

	/**
	 * Función que se ejecuta para realizar la acción
	 */
	@Override
	public void execute() throws Exception {

		// Verificamos si hay una factura seleccionada
		if (getView().getValue("oid") == null) {

			// Si no es así, mostramos un error
			addError("no_delete_not_exists");

		} else {
			
			// Creamos una carpeta en local para almacenar los archivos de prueba
			new File("C:\\Tempo").mkdir();

			// Creamos un objeto PrintWriter para generar un fichero para probar la subida 
			PrintWriter out = new PrintWriter("c:\\Tempo\\Prueba3.txt");

			// Rellenamos el fichero de contenido
			for (int i = 0; i < 50; i++) {
				out.println("Linea " + i);
			}

			// Cerramos el fichero dejandolo listo para la subida
			out.close();

			
			// Creamos el objeto FTPClient
			FTPClient cliente = new FTPClient();

			// Establecemos el sistema de transmisión de la información en modo
			// automático
			cliente.setType(FTPClient.TYPE_AUTO);

			// Establecemos la conexión como pasiva
			cliente.setPassive(true);

			// Conectamos al servidor
			cliente.connect("192.168.1.1", 2121);

			// Hacemos login con los datos de usuario y contraseña
			cliente.login("123456", "123456");

			// Cambiamos a un directorio específico dentro del ftp
			cliente.changeDirectory("pruebas");
			
			// Subimos el fichero que hemos creado anteriormente al directorio remoto actual
			cliente.upload(new java.io.File("C:\\Tempo\\Prueba3.txt"));

			// Nos desconectamos del servidor ftp
			cliente.disconnect(true);
		}

	}

}

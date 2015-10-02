package org.openxava.invoicing.actions;

import java.io.*;

import it.sauronsoftware.ftp4j.*;

import org.openxava.actions.*;

public class DownloadFile extends ViewBaseAction {

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

			// Creamos una carpeta en local para almacenar los archivos
			// descargados
			new File("C:\\Tempo").mkdir();

			// Iteramos por todos los ficheros que hay en el directorio remoto
			// del ftp
			for (FTPFile file : cliente.list()) {

				// Recuperamos el nombre del fichero actual
				String nombreFichero = file.getName();

				// Descargamos el fichero y lo guardamos dentro de la carpeta
				// que hemos creado con el mismo nombre que tenia en el servidor
				cliente.download(nombreFichero, new java.io.File("C:\\Tempo\\"
						+ nombreFichero));

			}

			// Desconectamos del servidor ftp
			cliente.disconnect(true);
		}
	}
}

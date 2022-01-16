package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.Normalizer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import modelo.ListadoPalabras;

/**
 * Clase que genera el servidor que almacena el ListadoPalabras y gestiona los
 * endpoints
 * 
 * @author Francisco Miguel Gonzalez Herrera, Rafael Moreno Estevez y José Alfonso Rodríguez Cabrero
 *
 */
public class Server {
	public static void main(String[] args) throws IOException {

		// Persistencia y carga de datos
		ListadoPalabras lp = new ListadoPalabras();
		File fichero = new File("listado_palabras.dat");

		if (fichero.exists()) {

			try (FileInputStream fis = new FileInputStream(fichero);
					ObjectInputStream ois = new ObjectInputStream(fis)) {

				lp = (ListadoPalabras) ois.readObject();

				System.out.println("Importación correcta de datos.....");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int port = 12345;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		System.out.println("Server started at " + port);

		server.createContext("/almacena", new PostHandler(lp, fichero));
		server.createContext("/consulta", new GetHandler(lp));
		server.createContext("/vaciar", new DeleteHandler(lp, fichero));
		server.createContext("/listado", new ListHandler(lp));
		server.setExecutor(null);
		server.start();
	}

	private static class PostHandler implements HttpHandler {

		ListadoPalabras lp;
		File fichero;

		public PostHandler(ListadoPalabras lp, File fichero) {
			this.lp = lp;
			this.fichero = fichero;
		}

		@Override
		public void handle(HttpExchange t) throws IOException {

			String requestMethod = t.getRequestMethod();

			if (requestMethod.equalsIgnoreCase("POST")) {

				// Eliminamos los posibles acentos de las cadenas antes de añadirlos al fichero
				String result = Normalizer.normalize(t.getRequestURI().getQuery(), Normalizer.Form.NFD);
				result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

				lp.addString(result);

				try (FileOutputStream fos = new FileOutputStream(fichero);
						ObjectOutputStream oos = new ObjectOutputStream(fos);) {

					oos.writeObject(lp);
					System.out.println("Cadena añadida y fichero guardado");

				} catch (Exception e) {
					e.printStackTrace();
				}

				String response = "Cadena agregada al fichero";
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

			} else {

				String response = "<h1>501 Not Implemented</h1>Method not implemented";
				t.sendResponseHeaders(501, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	private static class GetHandler implements HttpHandler {

		ListadoPalabras lp;

		public GetHandler(ListadoPalabras lp) {
			this.lp = lp;
		}

		@Override
		public void handle(HttpExchange t) throws IOException {

			String requestMethod = t.getRequestMethod();

			if (requestMethod.equalsIgnoreCase("GET")) {

				// Eliminamos los posibles acentos de las cadenas
				String result = Normalizer.normalize(t.getRequestURI().getQuery(), Normalizer.Form.NFD);
				result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
				
				String response = "Veces que la cadena se encuentra en el fichero: "
						+ String.valueOf(lp.searchString(result));

				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

			} else {

				String response = "<h1>501 Not Implemented</h1>Method not implemented";
				t.sendResponseHeaders(501, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	private static class DeleteHandler implements HttpHandler {

		ListadoPalabras lp;
		File fichero;

		public DeleteHandler(ListadoPalabras lp, File fichero) {
			this.lp = lp;
			this.fichero = fichero;
		}

		@Override
		public void handle(HttpExchange t) throws IOException {

			String requestMethod = t.getRequestMethod();

			if (requestMethod.equalsIgnoreCase("DELETE")) {

				lp.clear();

				try (FileOutputStream fos = new FileOutputStream(fichero);
						ObjectOutputStream oos = new ObjectOutputStream(fos);) {

					oos.writeObject(lp);
					System.out.println("Fichero vacidado y guardado");

				} catch (Exception e) {
					e.printStackTrace();
				}

				String response = "Contenido del fichero eliminado";
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

			} else {

				String response = "<h1>501 Not Implemented</h1>Method not implemented";
				t.sendResponseHeaders(501, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	private static class ListHandler implements HttpHandler {

		ListadoPalabras lp;

		public ListHandler(ListadoPalabras lp) {
			this.lp = lp;
		}

		@Override
		public void handle(HttpExchange t) throws IOException {

			String requestMethod = t.getRequestMethod();

			if (requestMethod.equalsIgnoreCase("GET")) {

				String response = "Contenido del fichero: " + lp.list();
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

			} else {

				String response = "<h1>501 Not Implemented</h1>Method not implemented";
				t.sendResponseHeaders(501, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}
}
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.MimetypesFileTypeMap;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// Waits for a connection request
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

	private static void HandleRequest(Socket s) {
		BufferedReader in;
		PrintWriter out;
		String request;

		try {
			
			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String req = "";
			request = in.readLine();
			System.out.println("--- Client request: " + request);
			while(in.ready()) {
				in.readLine();
			}
			if(request.contains("html")) {
				req = "text/html";
			}else if(request.contains("js")) {
				req = "application/javascript";
			}else if(request.contains("css")) {
				req = "text/css";
			}
			out = new PrintWriter(s.getOutputStream(), true);
			out.println("HTTP/1.0 200");
			out.println("Content-type: " + req);
			out.println("Server-name: myserver");
			out.println();
			String go = request.substring(5,request.length()-1);
			String to = go.substring(0, go.length()-8);
			System.out.println(to);
			if(to.equals("")) {
				to = "index.html";
			}
			String response = new String(Files.readAllBytes(Paths.get(to)));
			out.println(response);
			out.println("");
//			out.println("Content-length: " + response.length());
			out.flush();
			out.close();
			s.close();
		} catch (IOException e) {
			System.out.println("Failed respond to client request: " + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}
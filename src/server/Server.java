package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import common.Utils;

public class Server {

	// Local Host padr�o
	public static final String HOST = "127.0.0.1"; // IP fixo
	public static final int PORT = 4444; // Porta fixa
	// ADENDO: Para hostear p/ fora, utilizar ip da m�quina
	private ServerSocket server;
	private Map<String, ClientListener> clients;

	public Server() {
		try {
			String connection_info;
			clients = new HashMap<String, ClientListener>();
			// Inicializando o servidor
			server = new ServerSocket(PORT);
			System.out.println("Servidor Iniciado no host: " + HOST + " e porta: " + PORT);
			while (true) {
				// Aceitando conex�o vinda do servidor
				Socket connection = server.accept(); // aceita conex�o vinda de um usu�rio online
				// Recebe as mensagens da classe Utils
				connection_info = Utils.receivedMessage(connection);
				if (checkLogin(connection_info)) {
					ClientListener cl = new ClientListener(connection_info, connection, this);
					clients.put(connection_info, cl);
					Utils.sendMessage(connection, "SUCESS");
					new Thread(cl).start();
				} else {
					Utils.sendMessage(connection, "ERROR");
				}
			}
		} catch (IOException e) {
			System.err.println("[ERROR:Server] -> " + e.getMessage());
		}
	}

	public Map<String, ClientListener> getClients() {
		return clients;
	}

	private boolean checkLogin(String connection_info) {
		String[] splited = connection_info.split(":");
		// tratando o erro de apelidos e portas iguais
		for (Map.Entry<String, ClientListener> pair : clients.entrySet()) {
			String[] parts = pair.getKey().split(":");
			if (parts[0].toLowerCase().equals(splited[0].toLowerCase())) {
				return false;
			} else if ((parts[1] + parts[2]).equals(splited[1] + splited[2])) {
				return false;
			}
		}
		return true;
	}
}

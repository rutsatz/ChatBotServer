package chatbotserver;

import controller.ChatBotServerController;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Rafael Rutsatz
 */
public class ChatBotServer extends Application {

	public static ChatBotServer aplicacao;

	private ChatBotServerController controller;

	private ServerSocket server; // Socket do servidor.
	private ServerSocket server1; // Socket do servidor.
	private int contador = 1; // Contador do número de conexões.

	public static ObservableList<Cliente> clientes;
	public static ObservableList<Cliente> clientes1;

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {

		aplicacao = this;
		clientes = FXCollections.observableArrayList();
		clientes1 = FXCollections.observableArrayList();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatBotServerLayout.fxml"));

		Parent root = loader.load();
		controller = loader.getController();

		controller.clientes.setItems(clientes);
		controller.clientes1.setItems(clientes1);

		Scene scene = new Scene(root);

		stage.setHeight(500);
		stage.setWidth(700);
		
		
		stage.setTitle("Servidor Chat Bot");
		stage.setScene(scene);
		stage.show();

		stage.setOnCloseRequest((WindowEvent e) -> {
			fecharConexao();
			fecharConexao1();
		});

	}

	public void rodarServidor() {

		try {
			int porta = Integer.parseInt(controller.txPorta.getText());
			int limiteClientes = Integer.parseInt(controller.txConexoes.getText());

			// cria o ServerSocket
			server = new ServerSocket(porta, limiteClientes);

			while (controller.isServerRunning.get()) {

				Socket clientSocket = null;

				try {
					clientSocket = aguardaConexao(); // espera uma conexão.

					Cliente c = new Cliente(clientSocket, controller);
					
					// Atualiza a lista de clientes conectados.
					Platform.runLater(() -> {
						ChatBotServer.clientes.add(c);

					});
					
					Thread t = new Thread(c);
					t.start();

				} catch (SocketException socketException) {
					exibirMensagem("\nServidor parado");
					contador = 1;
					fecharConexao();
					Platform.runLater(() -> {
						controller.isServerRunning.set(false);
					});
				} catch (EOFException eOFException) {
					exibirMensagem("\nServidor encerrou a conexão.");
				} finally {
					contador++;
				}
			}

		} catch (IOException iOException) {
			exibirMensagem("\nNão foi possível iniciar o servidor!");
			iOException.printStackTrace();
		} finally {
			if (!controller.isServerRunning.get())
				fecharConexao(); // fecha a conexão.
		}

	}

	private Socket aguardaConexao() throws IOException {

		exibirMensagem("\nAguardando conexão");

		Socket conexao = server.accept(); // permite que o servidor aceite a
											// conexão.
		exibirMensagem("\nConexão " + contador + " recebida de: " + conexao.getInetAddress().getHostName());
		return conexao;
	}

	public void fecharConexao() {
		exibirMensagem("\nFechando conexão");
		System.out.println("Fechando conexão");
		if (server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!clientes.isEmpty()) {
			try {
				for (Cliente cliente : clientes) {

					ObjectOutputStream output = cliente.getOutput();
					ObjectInputStream input = cliente.getInput();
					Socket conexao = cliente.getConexao();
					try {
						cliente.finalize();

					} catch (Throwable e) {
						e.printStackTrace();
					}
					output.close();
					input.close();
					conexao.close();

				}
				Platform.runLater(() -> {
					clientes.clear();
				});
			} catch (IOException iOException) {
				iOException.printStackTrace();
			}
		}
	}

	private void exibirMensagem(String msg) {
		Platform.runLater(() -> {
			controller.taMensagens.appendText(msg);
		});
	}

	
	
	
	
	public void rodarServidor1() {

		try {
			int porta = Integer.parseInt(controller.txPorta1.getText());
			int limiteClientes = Integer.parseInt(controller.txConexoes1.getText());

			// cria o ServerSocket
			server1 = new ServerSocket(porta, limiteClientes);

			while (controller.isServerRunning1.get()) {

				Socket clientSocket = null;

				try {
					clientSocket = aguardaConexao1(); // espera uma conexão.

					
					
					Cliente c = new Cliente(clientSocket, controller);
					
					// Atualiza a lista de clientes conectados.
					Platform.runLater(() -> {
						ChatBotServer.clientes1.add(c);

					});

					
					Thread t = new Thread(c);
					t.start();

				} catch (SocketException socketException) {
					exibirMensagem("\nServidor parado 1");
//					contador = 1;
					fecharConexao1();
					Platform.runLater(() -> {
						controller.isServerRunning1.set(false);
					});
				} catch (EOFException eOFException) {
					exibirMensagem("\nServidor encerrou a conexão 1.");
				} finally {
//					contador++;
				}
			}

		} catch (IOException iOException) {
			exibirMensagem1("\nNão foi possível iniciar o servidor! 1");
			iOException.printStackTrace();
		} finally {
			if (!controller.isServerRunning1.get())
				fecharConexao1(); // fecha a conexão.
		}

	}

	private Socket aguardaConexao1() throws IOException {

		exibirMensagem1("\nAguardando conexão 1");

		Socket conexao = server1.accept(); // permite que o servidor aceite a
											// conexão.
		exibirMensagem1("\nConexão " + contador + " recebida de: " + conexao.getInetAddress().getHostName());
		return conexao;
	}

	public void fecharConexao1() {
		exibirMensagem1("\nFechando conexão");
		System.out.println("Fechando conexão 1");
		if (server1 != null && !server1.isClosed()) {
			try {
				server1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!clientes1.isEmpty()) {
			try {
				for (Cliente cliente : clientes1) {

					ObjectOutputStream output = cliente.getOutput();
					ObjectInputStream input = cliente.getInput();
					Socket conexao = cliente.getConexao();
					try {
						cliente.finalize();

					} catch (Throwable e) {
						e.printStackTrace();
					}
					output.close();
					input.close();
					conexao.close();

				}
				Platform.runLater(() -> {
					clientes1.clear();
				});
			} catch (IOException iOException) {
				iOException.printStackTrace();
			}
		}
	}

	private void exibirMensagem1(String msg) {
		Platform.runLater(() -> {
			controller.taMensagens1.appendText(msg);
		});
	}
	
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
}

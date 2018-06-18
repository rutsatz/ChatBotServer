package chatbotserver;

import controller.ChatBotServerController;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

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
	private int contador = 1; // Contador do n�mero de conex�es.

	public static ObservableList<Cliente> clientes;
	public static ObservableList<Cliente> clientes1;

	/*
	 * (non-Javadoc)
	 * 
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

		Map<String, Object> namespace = loader.getNamespace();
		
		stage.setHeight(500);
		stage.setWidth(700);

		stage.setTitle("Servidor Chat Bot");
		stage.setScene(scene);
		stage.show();

//		System.out.println("root.lookup(\"circle1\")" + root.lookup("#circle1"));
		
		stage.setOnCloseRequest((WindowEvent e) -> {

			System.out.println("controller.sbp.get() " + controller.sbp.get());
			System.out.println("controller.sbp1.get() " + controller.sbp1.get());
			
			controller.worker.cancel();
			controller.worker1.cancel();
			
			System.out.println("controller.sbp.get() " + controller.sbp.get());
			System.out.println("controller.sbp1.get() " + controller.sbp1.get());
			
			if (controller.sbp.get()) {
				fecharConexao();
			}
			if (controller.sbp1.get()) {
				fecharConexao1();
			}
		});

	}

	public void rodarServidor() {

		try {
			int porta = Integer.parseInt(controller.txPorta.getText());
			int limiteClientes = Integer.parseInt(controller.txConexoes.getText());

			// cria o ServerSocket
			server = new ServerSocket(porta, limiteClientes);

			while (controller.sbp.get()) {

				Socket clientSocket = null;

				try {
					clientSocket = aguardaConexao(); // espera uma conex�o.

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
					if (controller.sbp.get()) {
						fecharConexao();
					}
					// Platform.runLater(() -> {
					// controller.isServerRunning.set(false);
					// });
				} catch (EOFException eOFException) {
					exibirMensagem("\nServidor encerrou a conex�o.");
				} finally {
					contador++;
				}
			}

		} catch (IOException iOException) {
			exibirMensagem("\nN�o foi poss�vel iniciar o servidor!");
			iOException.printStackTrace();
		} finally {
			if (!controller.sbp.get())
				fecharConexao(); // fecha a conex�o.
		}

	}

	private Socket aguardaConexao() throws IOException {

		exibirMensagem("\nAguardando conex�o");

		Socket conexao = server.accept(); // permite que o servidor aceite a
											// conex�o.
		exibirMensagem("\nConex�o " + contador + " recebida de: " + conexao.getInetAddress().getHostName());
		return conexao;
	}

	public void fecharConexao() {
//		exibirMensagem("\nFechando conex�o");
//		System.out.println("Fechando conex�o");
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

			while (controller.sbp1.get()) {

				Socket clientSocket = null;

				try {
					clientSocket = aguardaConexao1(); // espera uma conex�o.

					Cliente c = new Cliente(clientSocket, controller);

					// Atualiza a lista de clientes conectados.
					Platform.runLater(() -> {
						ChatBotServer.clientes1.add(c);

					});

					Thread t = new Thread(c);
					t.start();

				} catch (SocketException socketException) {
					exibirMensagem("\nServidor parado 1");
					// contador = 1;
					if (controller.sbp1.get()) {
						fecharConexao1();
					}
					// Platform.runLater(() -> {
					// controller.isServerRunning1.set(false);
					// });
				} catch (EOFException eOFException) {
					exibirMensagem("\nServidor encerrou a conex�o 1.");
				} finally {
					// contador++;
				}
			}

		} catch (IOException iOException) {
			exibirMensagem1("\nN�o foi poss�vel iniciar o servidor! 1");
			iOException.printStackTrace();
		} finally {
			if (controller.sbp1.get())
				fecharConexao1(); // fecha a conex�o.
		}

	}

	private Socket aguardaConexao1() throws IOException {

		exibirMensagem1("\nAguardando conex�o 1");

		Socket conexao = server1.accept(); // permite que o servidor aceite a
											// conex�o.
		exibirMensagem1("\nConex�o " + contador + " recebida de: " + conexao.getInetAddress().getHostName());
		return conexao;
	}

	public void fecharConexao1() {
		exibirMensagem1("\nFechando conex�o");
		System.out.println("Fechando conex�o 1");
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

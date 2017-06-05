package chatbotserver;

import controller.ChatBotServerController;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
	private int contador = 1; // Contador do número de conexões.

	public static ObservableList<Cliente> clientes;

	@Override
	public void start(Stage stage) throws Exception {

		aplicacao = this;
		clientes = FXCollections.observableArrayList();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatBotServerLayout.fxml"));

		Parent root = loader.load();
		controller = loader.getController();

		controller.clientes.setItems(clientes);

		Scene scene = new Scene(root);

		stage.setTitle("Servidor Chat Bot");
		stage.setScene(scene);
		stage.show();

		stage.setOnCloseRequest((WindowEvent e) -> {
			fecharConexao();
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
					Thread t = new Thread(c);
					t.start();

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
//		if (!clientes.isEmpty()) {
			try {
//				for (Cliente cliente : clientes) {
//
//					ObjectOutputStream output = cliente.getOutput();
//					ObjectInputStream input = cliente.getInput();
//					Socket conexao = cliente.getConexao();
//					try {
//						cliente.finalize();
//
//					} catch (Throwable e) {
//						e.printStackTrace();
//					}
//					output.close();
//					input.close();
//					conexao.close();
				if(!server.isClosed()){
					server.close();}

//				}
//				Platform.runLater(() -> {
//					clientes.clear();
//				});
			} catch (IOException iOException) {
				iOException.printStackTrace();
			}
//		}
	}

	private void exibirMensagem(String msg) {
		Platform.runLater(() -> {
			controller.taMensagens.appendText(msg);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}

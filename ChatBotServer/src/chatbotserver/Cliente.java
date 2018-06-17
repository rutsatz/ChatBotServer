package chatbotserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import controller.ChatBotServerController;
import javafx.application.Platform;

public class Cliente implements Runnable {

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	private ChatBotServerController controller;

	private ObjectOutputStream output; // gera fluxo de saída para o cliente.
	private ObjectInputStream input; // gera fluxo de entrada a partir do
										// cliente.
	private Socket conexao; // conexão com o cliente.

	public Cliente(Socket conexao, ChatBotServerController controller) throws IOException {
		// super();
		this.conexao = conexao;
		this.output = new ObjectOutputStream(conexao.getOutputStream());
		this.input = new ObjectInputStream(conexao.getInputStream());

		this.controller = controller;

	}

	@Override
	/**
	 * Implementação da ação da Thread.
	 */
	public void run() {

		// Responde ao cliente que aceitou sua conexão.
		enviarDados(
				"Conexão estabelecida com sucesso" + "\nDigite \"\\help\" para ver a lista de comandos disponíveis");


		try {
			// Loop que fica recebendo as mensagens desse cliente.
			processaConexao();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// envia mensagem para o cliente.
	public void enviarDados(String mensagem) {

		try { // envia o objeto ao cliente.
			output.writeObject(mensagem);
			output.flush(); // esvazia a saída para o cliente.
			// exibirMensagem(mensagem);
		} catch (IOException iOException) {
			exibirMensagem("\nErro enviando objeto");
		}

	}

	/**
	 * Trata as mensagens recebidas desse cliente.
	 * 
	 * @throws IOException
	 */
	private void processaConexao() throws IOException {

		String mensagem = null;
		String resposta = null;

		// processa as mensagens enviadas pelo cliente
		do {
			try { // lê e exibe a mensagem
				try {
					mensagem = (String) input.readObject();
				} catch (IOException ioException) {
					exibirMensagem("\nCliente " + this + " saiu.");
					Platform.runLater(() -> {
						ChatBotServer.clientes.remove(this);
					});
					return;
				}

				exibirMensagem("\n" + conexao.getInetAddress().getHostName() + " enviou: " + mensagem);

				// Realiza os tratamentos para cada comando.
				resposta = new Mensagem(mensagem, output).getResponse();

				exibirMensagem("\nResposta (" + conexao.getInetAddress().getHostName() + "): " + resposta);
				enviarDados(resposta);

				// }catch (SocketException socketException) {
				// System.out.println("Cliente encerrou conexão");
			} catch (ClassNotFoundException classNotFoundException) {
				exibirMensagem("\nTipo de objeto desconhecido recebido");
			}
		} while (!mensagem.equals("encerrar"));

	}

	private void exibirMensagem(String msg) {
		Platform.runLater(() -> {
			controller.taMensagens.appendText(msg);
		});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(conexao.getInetAddress().getHostName() + " ");
		sb.append("(" + conexao.getInetAddress().getHostAddress() );
		sb.append(":"+conexao.getPort()+")");
		return sb.toString();
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public Socket getConexao() {
		return conexao;
	}

}

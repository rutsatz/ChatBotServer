package chatbotserver;

import java.io.ObjectOutputStream;

/**
 * Classe responsável por receber as mensagens do clientes e responder.
 * 
 * @author rutsa
 *
 */
public class Mensagem {

	private String comando;
	private ChatBotServer server;
	private ObjectOutputStream output;

	public Mensagem(String comando, ObjectOutputStream output) {
		this.comando = comando;
		this.output = output;
		server = ChatBotServer.aplicacao;
	}

	public String getResponse() {

		// Se for um comando. (Começa com /)
		if (comando.charAt(0) == '\\') {
			return trataComandos(comando);
		} else {
			return comando;
		}
	}

	private String trataComandos(String comando) {

		if (comando.matches("\\\\help")) {

			return "\\autores" + "\n\t\t\t\\trendtwitter" + "\n\t\t\t\\pergunta <sua pergunta sobre programacao>";

		} else if (comando.matches("\\\\autores")) {

			return "Rafael Fernando Rutsatz, Luis Fernando de Barros";

		} else if (comando.matches("\\\\trendtwitter")) {

			return "Top 10 trends mundiais:\n" + new Twitter().getTrends() + "\n";

		} else if (comando.contains("\\pergunta")) {

			return "Perguntas relacionadas: \n" + new StackOverflow(comando).getAnswer() + "\n";

		} else {

			return "comando inválido (Digite \\help para ver a lista de comandos disponíveis)";

		}

	}

}

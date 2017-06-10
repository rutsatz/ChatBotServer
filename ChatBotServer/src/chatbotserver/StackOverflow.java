package chatbotserver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StackOverflow {

	private String site = "stackoverflow";
	private String pergunta;

	private String comando;

	ObservableList<Question> answer = FXCollections.observableArrayList();

	public StackOverflow(String comando) {
		this.comando = comando;

	}

	/**
	 * Extrai a pergunta do comando bruto.
	 * 
	 * @return
	 */
	private String getPergunta(String comando) throws Exception {

		String pergunta;

		pergunta = comando.substring(10);

		if (pergunta.length() == 0) {
			throw new Exception("Nenhuma pergunta informada.");
		}

		return pergunta;
	}

	public String getAnswer() {

		try {
			// extrai o parâmetro da string.
			pergunta = getPergunta(comando);

			String url = encodeString("http://api.stackexchange.com/2.2/search?tagged=" + pergunta + "&site=" + site);

			// Criamos um objeto URL que aponta para o WebService.
			URL host = new URL(url);

			/**
			 * Consome REST. Abrimos a conexão. A API StackExchange envia os
			 * dados em zip, por isso precisamos abrir GZIPInputStream e
			 * passamos o InputStream obtido da conexão.
			 */
			JsonReader jr = Json.createReader(new GZIPInputStream(host.openConnection().getInputStream()));

			// obtém o arquivo JSON bruto
			JsonObject jsonObject = jr.readObject();

			// Extrai o array de itens, com as respostas.
			JsonArray jsonArray = jsonObject.getJsonArray("items");

			// Converte cada item num Java Bean e adiciona na lista.
			jsonArray.iterator().forEachRemaining((JsonValue e) -> {
				JsonObject obj = (JsonObject) e;
				JsonString name = obj.getJsonObject("owner").getJsonString("display_name");
				JsonString quest = obj.getJsonString("title");
				JsonNumber jsonNumber = obj.getJsonNumber("creation_date");
				Question q = new Question(name.getString(), quest.getString(), jsonNumber.longValue() * 1000);
				answer.add(q);
			});

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			return "Erro ao consumir web service.\n" + "Verifique se sua pergunta está digitada corretamente.\n"
					+ "Retorno do servidor: " + e.getMessage();
		} catch (Exception e) {

			return e.getMessage();
		}

		return this.toString();
	}

	@Override
	public String toString() {
		int qtd = 10;
		if (answer.size() == 0) {
			return "Nenhum resultado para o filtro informado!";
		} else if (answer.size() < 10) {
			qtd = answer.size();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < qtd; i++) {
			Question q = answer.get(i);
			sb.append("\nUsuário: " + q.getOwner() + "\t(" + q.getTimestampString() + ")\n");
			sb.append("Pergunta: " + q.getQuestion() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Coloca % no lugar do espaço para passar a url como parâmetro.
	 * 
	 * @param palavra
	 * @return
	 */
	public String encodeString(String palavra) {
		char one;
		StringBuffer n = new StringBuffer(palavra.length());
		for (int i = 0; i < palavra.length(); i++) {
			one = palavra.charAt(i);
			switch (one) {
			case ' ':
				n.append('%');
				n.append('2');
				n.append('0');
				break;
			default:
				n.append(one);
			}
		}
		return n.toString();
	}

	/**
	 * Java Bean para cada questão.
	 * 
	 * @author rutsa
	 *
	 */
	public class Question {

		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");

		private StringProperty ownerProperty = new SimpleStringProperty();
		private String question;
		private long timestamp;

		public Question(String o, String q, long t) {
			this.ownerProperty.set(o);
			this.question = q;
			this.timestamp = t;
		}

		public String getOwner() {
			return ownerProperty.get();
		}

		public void setOwner(String owner) {
			this.ownerProperty.set(owner);
		}

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public String getTimestampString() {
			return sdf.format(new Date(timestamp));
		}
	}
}

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Rafael Rutsatz
 */
public class ChatBotServer extends Application {

    public static ChatBotServer aplicacao;

    private ChatBotServerController controller;

    private ObjectOutputStream output; // gera fluxo de saída para o cliente.
    private ObjectInputStream input; // gera fluxo de entrada a partir do cliente.
    
    private ServerSocket server; // Socket do servidor.
    private Socket conexao; // conexão com o cliente.
    private int contador = 1; // Contador do número de conexões.

    @Override
    public void start(Stage stage) throws Exception {

        aplicacao = this;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatBotServerLayout.fxml"));

        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);

        stage.setTitle("Servidor Chat Bot");
        stage.setScene(scene);
        stage.show();
    }

    public void rodarServidor() {

        try {
            int porta = Integer.parseInt(controller.txPorta.getText());
            int limiteClientes = Integer.parseInt(controller.txConexoes.getText());

            // cria o ServerSocket
            server = new ServerSocket(porta, limiteClientes);

            while (true) {
                try {
                    aguardaConexao(); // espera uma conexão.
                    getStreams(); // obtém os fluxos de entrada e saída.
                    processaConexao(); // processa a conexão.
                } catch (EOFException eOFException) {
                    exibirMensagem("Servidor encerrou a conexão.");
                } finally {
                    fecharConexao(); // fecha a conexão.
                    contador++;
                }
            }

        } catch (IOException iOException) {
            exibirMensagem("Não foi possível iniciar o servidor!");
            iOException.printStackTrace();
        }

        
    }

    private void aguardaConexao() throws IOException{
        exibirMensagem("Aguardando conexão");
        conexao = server.accept(); // permite que o servidor aceite a conexão.
        exibirMensagem("Conexão " + contador + " recebida de: " + 
                conexao.getInetAddress().getHostName());
    }
    
    private void getStreams() throws IOException{
        
        // configura o fluxo de saída para objetos.
        output = new ObjectOutputStream(conexao.getOutputStream());
        output.flush(); // esvazia buffer de saída para enviar as informações de cabeçalho.
        
        // configura o fluxo de entrada para objetos.
        input = new ObjectInputStream(conexao.getInputStream());
        exibirMensagem("Obteve Streams de entrada e saída");
    }
    
    private void processaConexao() throws IOException{
        
        String mensagem = null;
        
        enviarDados("Conexão estabelecida com sucesso");
        
        // processa as mensagens enviadas pelo cliente
        do{
            try{ // lê e exibe a mensagem
                mensagem = (String) input.readObject();
                exibirMensagem(mensagem);
                enviarDados("recebido, agora já pode jogar lol");
            }catch(ClassNotFoundException classNotFoundException){
                exibirMensagem("Tipo de objeto desconhecido recebido");
            }
        }while(!mensagem.equals("encerrar"));
        
    }
    
    public void fecharConexao(){
        exibirMensagem("Fechando conexão");
        try{
            output.close();
            input.close();
            conexao.close();
        }catch(IOException iOException){
            iOException.printStackTrace();
        }
    }
    
    // envia mensagem para o cliente.
    private void enviarDados(String mensagem){
        
        try{ // envia o objeto ao cliente.
            output.writeObject(mensagem);
            output.flush(); // esvazia a saída para o cliente.
            exibirMensagem(mensagem);
        }catch(IOException iOException){
            exibirMensagem("Erro enviando objeto");
        }
        
    }
    
    private void exibirMensagem(String msg) {
        Platform.runLater(() -> {
            controller.taMensagens.appendText(msg + "\n");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

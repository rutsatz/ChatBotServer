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

    private ObjectOutputStream output; // gera fluxo de sa�da para o cliente.
    private ObjectInputStream input; // gera fluxo de entrada a partir do cliente.
    
    private ServerSocket server; // Socket do servidor.
    private Socket conexao; // conex�o com o cliente.
    private int contador = 1; // Contador do n�mero de conex�es.

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
                    aguardaConexao(); // espera uma conex�o.
                    getStreams(); // obt�m os fluxos de entrada e sa�da.
                    processaConexao(); // processa a conex�o.
                } catch (EOFException eOFException) {
                    exibirMensagem("Servidor encerrou a conex�o.");
                } finally {
                    fecharConexao(); // fecha a conex�o.
                    contador++;
                }
            }

        } catch (IOException iOException) {
            exibirMensagem("N�o foi poss�vel iniciar o servidor!");
            iOException.printStackTrace();
        }

        
    }

    private void aguardaConexao() throws IOException{
        exibirMensagem("Aguardando conex�o");
        conexao = server.accept(); // permite que o servidor aceite a conex�o.
        exibirMensagem("Conex�o " + contador + " recebida de: " + 
                conexao.getInetAddress().getHostName());
    }
    
    private void getStreams() throws IOException{
        
        // configura o fluxo de sa�da para objetos.
        output = new ObjectOutputStream(conexao.getOutputStream());
        output.flush(); // esvazia buffer de sa�da para enviar as informa��es de cabe�alho.
        
        // configura o fluxo de entrada para objetos.
        input = new ObjectInputStream(conexao.getInputStream());
        exibirMensagem("Obteve Streams de entrada e sa�da");
    }
    
    private void processaConexao() throws IOException{
        
        String mensagem = null;
        
        enviarDados("Conex�o estabelecida com sucesso");
        
        // processa as mensagens enviadas pelo cliente
        do{
            try{ // l� e exibe a mensagem
                mensagem = (String) input.readObject();
                exibirMensagem(mensagem);
                enviarDados("recebido, agora j� pode jogar lol");
            }catch(ClassNotFoundException classNotFoundException){
                exibirMensagem("Tipo de objeto desconhecido recebido");
            }
        }while(!mensagem.equals("encerrar"));
        
    }
    
    public void fecharConexao(){
        exibirMensagem("Fechando conex�o");
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
            output.flush(); // esvazia a sa�da para o cliente.
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

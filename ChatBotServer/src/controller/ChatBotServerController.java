package controller;

import chatbotserver.ChatBotServer;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Rafael Rutsatz
 */
public class ChatBotServerController implements Initializable {

    @FXML
    public TextArea taMensagens;

    @FXML
    public TextField txPorta;
    
    @FXML
    public TextField txConexoes;

    @FXML
    public Button btIniciar;
    
    @FXML
    public Button btParar;
    
    @FXML
    public ListView clientes;
    
    private Worker<String> worker;
    
    public BooleanProperty isServerRunning = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Implementa Worker rodando por thread. (Está usando classe anônima
         * para rodar uma Task.)
         */
        worker = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        ChatBotServer.aplicacao.rodarServidor();
                        return "iniciado";
                    }
                };
            }

            @Override
            /**
             * Função chamada ao usuário cancelar a Task.
             */
            protected void cancelled() {
            	Platform.runLater(() -> {
                	isServerRunning.set(false);
                });
                ChatBotServer.aplicacao.fecharConexao();
                
                System.out.println("cancelou");
            }

            
            
        };
     
        // realiza os binds
        btIniciar.disableProperty().bind(isServerRunning);
        btParar.disableProperty().bind(Bindings.createBooleanBinding(() -> {
        	if(isServerRunning.get()){
        		return false;
        	}
        	return true;
        }, isServerRunning));
    }

    @FXML
    private void iniciarServidor(ActionEvent e) {
        
        // Inicializa a execução da Thread.
        ((Service) worker).restart();
        Platform.runLater(() -> {
        	isServerRunning.set(true);
        });
        
    }

    @FXML
    private void pararServidor(ActionEvent e) {

        // Cancela a execução da Thread.
        worker.cancel();
//        Platform.runLater(() -> {
//        	isServerRunning.set(false);
//        });
        
    }
}

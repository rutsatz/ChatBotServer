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

    @FXML
    public TextArea taMensagens1;

    @FXML
    public TextField txPorta1;
    
    @FXML
    public TextField txConexoes1;

    @FXML
    public Button btIniciar1;
    
    @FXML
    public Button btParar1;
    
    @FXML
    public ListView clientes1;
    
    private Worker<String> worker1;
    
    public BooleanProperty isServerRunning1 = new SimpleBooleanProperty(false);
    
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
        
        
        
        
        /**
         * Implementa Worker rodando por thread. (Está usando classe anônima
         * para rodar uma Task.)
         */
        worker1 = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        ChatBotServer.aplicacao.rodarServidor1();
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
                	isServerRunning1.set(false);
                });
                ChatBotServer.aplicacao.fecharConexao1();
                
                System.out.println("cancelou 1");
            }

            
            
        };
     
        // realiza os binds
        btIniciar1.disableProperty().bind(isServerRunning1);
        btParar1.disableProperty().bind(Bindings.createBooleanBinding(() -> {
        	if(isServerRunning1.get()){
        		return false;
        	}
        	return true;
        }, isServerRunning1));
        
        
        
        
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
    
    @FXML
    private void iniciarServidor1(ActionEvent e) {
        
        // Inicializa a execução da Thread.
        ((Service) worker1).restart();
        Platform.runLater(() -> {
        	isServerRunning1.set(true);
        });
        
    }

    @FXML
    private void pararServidor1(ActionEvent e) {

        // Cancela a execução da Thread.
        worker1.cancel();
//        Platform.runLater(() -> {
//        	isServerRunning.set(false);
//        });
        
    }
    
}

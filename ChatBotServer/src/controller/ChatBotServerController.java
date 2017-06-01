package controller;

import chatbotserver.ChatBotServer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    
    private Worker<String> worker;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Implementa Worker rodando por thread. (Est� usando classe an�nima
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
             * Fun��o chamada ao usu�rio cancelar a Task.
             */
            protected void cancelled() {
                ChatBotServer.aplicacao.fecharConexao();
                System.out.println("cancelou");
            }

            
            
        };
        
    }

    @FXML
    private void iniciarServidor(ActionEvent e) {
        
        // Inicializa a execu��o da Thread.
        ((Service) worker).restart();
        
    }

    @FXML
    private void pararServidor(ActionEvent e) {

        // Cancela a execu��o da Thread.
        worker.cancel();
        
    }
}

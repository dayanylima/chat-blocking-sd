package br.edu.ifnmg.sd.chat.blocking;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author dayan
 */
public class ChatClient implements Runnable {

    
    //endereço IP do servidor
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private ClientSocket clientSocket;
    private Scanner sc;

    public ChatClient() {
        sc = new Scanner(System.in);
    }

    public void start() throws IOException {

        try {
            //instacio o socket do cliente informadando qual o endereço e porta do servidor 
            clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));
            System.out.println("Cliente conectado ao servidor " + SERVER_ADDRESS + ":" + ChatServer.PORT);
            //Executa o método run, para receber mensagens da rede
            new Thread(this).start();
            //Chamo mensageLoop para enviar mensagens na rede
            messageLoop();
        } finally {
            clientSocket.close();
        }
    }

    //Essa thread fica recebendo mensagens do servidor, que foram enviados por outros clientes
    @Override
    public void run() {

        String mensagem;
        while ((mensagem = clientSocket.getMessage()) != null) {
            System.out.printf("Mensagem recebida do servidor: %s\n", mensagem);
        }
    }

    //Método responável por permitir o cliente ficar enviando mensagens
    private void messageLoop() throws IOException {
        String mensagem;
        do {
            System.out.println("Digite mensagem (ou sair para finalizar chat): ");
            mensagem = sc.nextLine();
            //Envio a mensagem para os outros usuário da rede
            clientSocket.sendMensagem(mensagem);
        } while (!mensagem.equalsIgnoreCase("sair"));
    }

    public static void main(String[] args) {

        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar cliene " + ex.getMessage());
        }

        System.out.println("Cliente finalizado!");
    }

}

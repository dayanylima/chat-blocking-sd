package br.edu.ifnmg.sd.chat.blocking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dayan
 */
public class ChatServer {

    //porta utilizada pelo servidor
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    //Lista de clientes do servidor
    private final List<ClientSocket> clients = new LinkedList<>();

    //Função que inicia o servidor
    public void start() throws IOException {

        //Instancio meu objeto serverSocket, informando a porta que vai utilizada para o servidor
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta " + PORT);
        clientConnectionLoop();
    }

    //Método responsável por ficar aguardando as conexões dos clientes.
    private void clientConnectionLoop() throws IOException {
       
        //Fico aguardando conexão de um cliente e crio um socket local para este cliente
        //e adiciono ele a lista de clientes do servidor.
        while (true) {
            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            clients.add(clientSocket);
            //Thread responsável por ficar recebendo mensagens do cliente
            new Thread(() -> clientMessageLoop(clientSocket)).start();

        }
    }

    //Recebo mensagens do cliente e encaminho para os demais clientes do servidor
    private void clientMessageLoop(ClientSocket clientSocket) {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                if ("sair".equalsIgnoreCase(mensagem)) {
                    return;
                }
                System.out.println("Mensagem recebida do cliente :"
                        + clientSocket.getRemotoSocketAddress() + ": " + mensagem);
                sendMensagemToAll(clientSocket, mensagem);

            }
        } finally {
            //fecho canais utilizados
            clientSocket.close();
        }

    }

    //Método responsável por encaminhar uma mensagem recebida para todos os clientes do servidor
    //Recebo e emissor e a mensagem
    private void sendMensagemToAll(ClientSocket sender, String mensagem) {
        Iterator<ClientSocket> iterator = clients.iterator();
        while (iterator.hasNext()) {
            ClientSocket clientSocket = iterator.next();
            //Evita que o servidor mande a mensagem de volta para o seu próprio remetente
            if (!sender.equals(clientSocket)) {
                //Se não foi possível enviar mensagem, remove elemento atual.
                //Então se conexão de cliente cair ou o cliente sair da aplicação
                //o servidor não vai mandar mensagem para ele.
                if (!clientSocket.sendMensagem("cliente " + sender.getRemotoSocketAddress() + ": " + mensagem)) {
                    //antes de enviar modifico a mensagem informando quem enviou
                    iterator.remove();
                }
            }
        }

    }

    public static void main(String[] args) {
        //Instacio o ChatServer e de fato iniciar o servidor, caso a porta esteja ocupada ou servidor
        //não for possível iniciar, é exibibida mensagem de erro.
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar o servidor: " + ex.getMessage());
        }

        System.out.println("Servidor finalizado");

    }
}

package br.edu.ifnmg.sd.chat.blocking;

import java.net.Socket;
import java.net.SocketAddress;
import java.io.*;

/**
 *
 * @author dayan
 */

//Classe para armazenar o socket do cliente
public class ClientSocket {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("Cliente " + socket.getRemoteSocketAddress() + " conectou");
        //Instacio objetos para receber e enviar mensagens dos clientes
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    //Método para exibir o endereço do cliente
    public SocketAddress getRemotoSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    //Fechar o socket e os objetos de entrada e saída, para liberar os recursos utilizados
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Erro ao fechar socket: " + ex.getMessage());
        }

    }

    //Método que faz a leitura da mensagem
    public String getMessage() {
        try {
            return in.readLine();
        } catch (IOException ex) {
            return null;
        }
    }
    
    //Método para enviar mensagem e informar se esta foi enviado ou não
    public boolean sendMensagem(String mensagem) {
        out.println(mensagem);
        return !out.checkError();
    }

}

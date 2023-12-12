package connection;

import server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements Runnable{
    private Server server;
    private ServerSocket socket;
    public Connection(Server server){
        this.server = server;
        try {
            this.socket = new ServerSocket(server.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        while(true){
            try {
                Socket clientSocket = this.socket.accept();
                ConnectedClient newClient = new ConnectedClient(server, clientSocket);

                server.addClient(newClient);
                Thread threadNewClient = new Thread(newClient);
                threadNewClient.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

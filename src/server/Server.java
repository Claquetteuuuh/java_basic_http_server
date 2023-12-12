package server;

import connection.ConnectedClient;
import connection.Connection;

import java.util.ArrayList;
import java.util.List;

public class Server {
    public static String routesPath = "src/routes/";
    private Integer port;
    private String route;
    private List<ConnectedClient> clients;
    public Server(Integer port){
        this.port = port;

        this.clients = new ArrayList<ConnectedClient>();
        Thread threadConnection = new Thread(new Connection(this));
        threadConnection.start();
    }
    public void addClient(ConnectedClient client){
        this.clients.add(client);
        System.out.println("Connection !");
    }

    public String getRouteFile(String route){
        String renderedRoute = this.routesPath + route + "/index.html";
        return renderedRoute;
    }

    public Integer getPort() {
        return port;
    }
}

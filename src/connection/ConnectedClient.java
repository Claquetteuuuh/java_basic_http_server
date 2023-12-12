package connection;

import server.Server;

import java.io.*;
import java.net.Socket;

public class ConnectedClient implements Runnable{
    private static Integer idCount = 0;
    private Integer id;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ConnectedClient(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
        this.id = idCount++;
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Nouvelle connection id: " + this.id);

    }

    public Integer getId() {
        return id;
    }

    @Override
    public void run() {
        byte receivedData[] = new byte[1000];
        try {
            in.read(receivedData, 0, 1000);
            String m = new String(receivedData);
            String lineOne = m.split("\n")[0];
            String method = lineOne.split("/")[0].trim();

            if(method.equalsIgnoreCase("GET")){
                String route = lineOne.split("/")[1].split("HTTP")[0].trim();
                if(route == ""){
                    route = "home";
                }
                InputStream inputStream = new FileInputStream(server.getRouteFile(route));
                out.writeBytes("HTTP/1.1 200 OK\r\n");
                out.writeBytes("Content-Type: text/html\r\n");

                out.writeBytes("\r\n");

                int byteRead = -1;
                while((byteRead = inputStream.read()) != -1 ){
                    out.write(byteRead);
                }

            }else{
                out.writeBytes("HTTP/1.1 400 Bad Request\r\n ");
                out.writeBytes("Content-Type: application/json; charset=utf-8\r\n");
                out.writeBytes("\r\n");
                out.writeBytes("{\"error\": \"Cette page n'accepte que les requetes GET !\"}");
            }

        } catch (IOException e) {
            try {
                out.writeBytes("HTTP/1.1 404 OK\r\n");
                out.writeBytes("Content-Type: application/json; charset=utf-8\r\n");
                out.writeBytes("\r\n");
                out.writeBytes("{\"error\": \"page pas existante !\"}");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

        try {
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

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
        // parsing
        try {
            in.read(receivedData, 0, 1000);

        } catch (IOException e) {
            throw new RuntimeException("QUOICOUEXEPTION DATA");
        }
        String m = new String(receivedData);
        String lineOne = m.split("\n")[0];
        String method = lineOne.split(" ")[0].trim();
        if(method.length() != 0){
            String route = lineOne.split(" ")[1];
            if(method.equalsIgnoreCase("GET")){
                if(route.equalsIgnoreCase("/")){
                    route = "home";
                }
                InputStream inputStream = null;
                Boolean findPage = true;
                try {
                    inputStream = new FileInputStream(server.getRouteFile(route));
                } catch (FileNotFoundException e) {
                    // 404
                    findPage = false;
                }
                if(findPage){
                    try{
                        out.writeBytes("HTTP/1.1 200 OK\r\n");
                        out.writeBytes("Content-Type: text/html\r\n");

                        out.writeBytes("\r\n");

                        int byteRead = -1;
                        while((byteRead = inputStream.read()) != -1 ){
                            out.write(byteRead);
                        }

                    } catch (IOException e) {
                        System.out.println("quoicoufonctionne pu");
                    }
                }else{
                    try {
                        out.writeBytes("HTTP/1.1 404 OK\r\n");
                        out.writeBytes("Content-Type: application/json; charset=utf-8\r\n");
                        out.writeBytes("\r\n");
                        out.writeBytes("{\"error\": \"page pas existante !\"}");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }else{
                // PAS GET
                try {
                    out.writeBytes("HTTP/1.1 400 Bad Request\r\n ");
                    out.writeBytes("Content-Type: application/json; charset=utf-8\r\n");
                    out.writeBytes("\r\n");
                    out.writeBytes("{\"error\": \"Cette page n'accepte que les requetes GET !\"}");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

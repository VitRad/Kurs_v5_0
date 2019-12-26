package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    // порт, который будет прослушивать наш сервер
    static final int PORT = 3443;
    // список клиентов, которые будут подключаться к серверу
    public ArrayList<ClientHandler> clients;
    public static ArrayList<String> history;
    public HashMap<ClientHandler, String> clientsMap;

    public void startServer() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        history = new ArrayList();
        clients = new ArrayList<>();
        clientsMap = new HashMap<>();
        try {
            // создаём серверный сокет
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");

            // запускаем бесконечный цикл
            while (true) {
                // ждём подключений от сервера
                clientSocket = serverSocket.accept();
                // создаём обработчик клиента, который подключился к серверу
                ClientHandler client = new ClientHandler();
                client.clientSession(clientSocket, this);
                clients.add(client);
                // каждое подключение клиента обрабатываем в новом потоке
                new Thread(client).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                // закрываем подключение
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }
    //Отправляем сообщение конкретному клиенту чата
    public void sendMessageToClient(ClientHandler client, String msg){
        client.sendMsg(msg);
    }

    //Вывод истории сообщений в консоль
    public void printHistory(){
        System.out.println("------Вывод истории сообщений:-------");
        for (String h: history){
            System.out.println(h);
        }
        System.out.println("------End history--------");
    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}

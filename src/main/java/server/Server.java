package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    // порт, который будет прослушивать наш сервер
    static final int PORT = 3443;
    // список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    public ArrayList<String> history;

    public Server() {
        history = new ArrayList();
        // Объявление сокета клиента
        Socket clientSocket = null;
        // Объявление серверного сокета
        ServerSocket serverSocket = null;
        try {
            // создаём серверный сокет
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
            // запускаем бесконечный цикл
            while (true) {
                // ждём подключений от сервера
                clientSocket = serverSocket.accept();
                // создаём обработчик клиента, который подключился к серверу
                ClientHandler client = new ClientHandler(clientSocket, this);
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

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}

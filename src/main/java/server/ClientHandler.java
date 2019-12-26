package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    // экземпляр нашего сервера
    private Server server;
    // исходящее сообщение
    private PrintWriter outMessage;
    // входящее собщение
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    // клиентский сокет
    private Socket clientSocket = null;
    // количество клиента в чате
    private static int clients_count = 0;

    // конструктор, который принимает клиентский сокет и сервер
    public void clientSession(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

        try {
            while (true) {
                // сервер отправляет сообщение
                server.sendMessageToAllClients("Новый участник вошёл в чат!");
                server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
                break;
            }

            while (true) {
                // Если от клиента пришло сообщение
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    // если клиент отправляет данное сообщение, то цикл прерывается и
                    // клиент выходит из чата
                    if (clientMessage.equals("exit")) {
                        break;
                    }
                    //Теперь проверим, если клиент уже есть в списке клиентов, то выделим имя из сообщения
                    //и заполним Мапу именем
                    for (ClientHandler cl : server.clients) {
                        if (cl.equals(this)) {
                            String clientName = findName(clientMessage)[0];
                            server.clientsMap.put(this, clientName);
                        }
                    }
                    //Если в сообщении указан конкретный получатель,
                    //Определим его экземпляр и отправим ему сообщение
                    if (findName(clientMessage).length > 2) {
                        String name = findName(clientMessage)[1].replaceAll("\\s", "");
                        for (Map.Entry e : server.clientsMap.entrySet()) {
                            if (e.getValue().equals(name)) {
                                String name1 = name + ": ";
                                String q = clientMessage.replaceAll(name1,"");
                                server.sendMessageToClient((ClientHandler) e.getKey(), q);
                                System.out.println("Отправили приватное сообщение клиенту: " + name);
                            }
                        }
                        //И не забываем отправить это сообщение для себя
                        server.sendMessageToClient(this, clientMessage);
                    } else //Иначе, отправляем сообщение ВСЕМ пользователям
                    {server.sendMessageToAllClients(clientMessage);}
                        //Сохраняем сообщение в общую историю сервера
                        server.history.add(clientMessage);
                        // выводим в консоль сообщение
                        System.out.println("clientMessage: " + clientMessage);
                        // отправляем данное сообщение всем клиентам
                        System.out.println("-----------------------");
                        System.out.println("Для проверки, что история записывается, выведем 2 последних сообщения: ");
                        int size = server.history.size();
                        System.out.println("history: " + server.history.get(size - 1));
                        if (size > 1) {
                            System.out.println("history: " + server.history.get(size - 2));
                        }
                        System.out.println("-----------------------");
                    }
                }
            }
        finally{
                this.close();
            }
        }

    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
    public String[] findName(String msg){
        String[] substr;
        String delimiter = ":";
        substr = msg.split(delimiter);
        return substr;
    }
}

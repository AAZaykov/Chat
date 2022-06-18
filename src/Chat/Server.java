package Chat;

import Chat.Connection;
import Chat.ConsoleHelper;
import Chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        for (Connection connection : connectionMap.values()
        ) {
            try {
                connection.send(message);
            } catch (IOException e) {
                System.out.println("Не смогли отправить сообщение!");
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            Message message;
            do {
                connection.send(new Message(MessageType.NAME_REQUEST));
                message = connection.receive();
            }while (message.getType() != MessageType.USER_NAME || message.getData() == "" ||
                    connectionMap.containsKey(message.getData()));
            connectionMap.put(message.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return message.getData();
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry pair : connectionMap.entrySet()
            ) {
                Message message = new Message(MessageType.USER_ADDED, (String) pair.getKey());
                if (!pair.getKey().equals(userName))
                    connection.send(message);
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true){
                Message message = connection.receive();
                if (message.getType() != MessageType.TEXT)
                    ConsoleHelper.writeMessage("Ошибка!");
                else if (message.getType() == MessageType.TEXT)
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
            }
        }

        public void run() {
            ConsoleHelper.writeMessage("New connection with " + socket.getRemoteSocketAddress());
            String userName = null;
            try (Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка при обмене данными с удаленным адресом!");
            }
            if (userName != null)
                connectionMap.remove(userName);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто!");

        }
    }

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Введите номер порта!");
        int port = ConsoleHelper.readInt();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен!");
            Socket socket;
            while (true){
                socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
                continue;
            }
        } catch (IOException e) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(e.getMessage());
        }
    }
}
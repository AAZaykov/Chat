package Chat.client;

import Chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды, курс валюты (почти любой). \n " +
                    "Для запроса курса валюты введите \"курс \" и код валюты");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] arr = message.split(": ");
            String userName;
            String text;
            if (arr.length == 2){
                if (message.contains("курс")) {
                    String[] arr1 = message.split(" ");
                    userName = arr1[0].replace(":", "");
                    sendTextMessage("Информация для " + userName + ": курс " + arr1[2] + " - " + CurrencyRate.rate(arr1[2]));
                }
                userName = arr[0];
                text = arr[1];
                switch (text){
                    case "дата":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("d.MM.YYYY").format(new GregorianCalendar().getTime()));
                        break;
                    case "день":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("d").format(new GregorianCalendar().getTime()));
                        break;
                    case "месяц":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("MMMM").format(new GregorianCalendar().getTime()));
                        break;
                    case "год":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("YYYY").format(new GregorianCalendar().getTime()));
                        break;
                    case "время":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("H:mm:ss").format(new GregorianCalendar().getTime()));
                        break;
                    case "час":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("H").format(new GregorianCalendar().getTime()));
                        break;
                    case "минуты":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("m").format(new GregorianCalendar().getTime()));
                        break;
                    case "секунды":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("s").format(new GregorianCalendar().getTime()));
                        break;
                    case "день недели":
                        sendTextMessage("Информация для " + userName + ": " + new SimpleDateFormat("EEEE").format(new GregorianCalendar().getTime()));
                        break;
                }
            }
        }
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    public static void main(String[] args) {
        new BotClient().run();
    }
}
package com.company;
/*Класс, реализующий всю работу сервера: запуск, ожидание клиента, общение с клиентом, закрытие.
 * Конструктор инициализирует сервер на локальном хосте, порт получает на вход.
 * Метод run ждет клиента и после его подключения запускат обработку - метод Process, после завершения
 * которого закрывает сервер.
 * Метод Process обрабатывает клиента - получает его сообщение и отправляет ответ.*/

import com.common.MessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static Socket clientSocket;
    private static ServerSocket serverSocket;
    private ClientMassage clientMessage = new ClientMassage();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Сервер запущен. Порт = " + port);
        } catch (IOException e) {
            logger.error("Сервер не запущен. " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("Сервер запущен");

        try {
            clientSocket = serverSocket.accept();
            logger.info("Принят клиент " + clientSocket);

            processClient();

        } catch (IOException e) {
            logger.error("Произошла ошибка во врмея работы с клиентом. " + e);
        } finally {
            closeServer();
        }
    }

    private void closeServer() {
        if (serverSocket != null) {
            try {
                System.out.println("Сервер закрыт.");
                serverSocket.close();
                logger.info("Сервер остановлен");
            } catch (IOException e) {
                logger.error("Не удалось остановить сервер. " + e);
            }
        }
    }

    private void processClient() {
        try {
            Boolean isProcessInputSuccessful = processInputMessage();
            processOutputMessage(isProcessInputSuccessful);
        } finally {
            closeClient();
        }
    }

    private Boolean processInputMessage() {
        String message = MessageUtils.getMessage(clientSocket);
        clientMessage = ClientMessageParser.parseClientMessage(message, true);
        if (clientMessage.getName() != null) {

            logger.info("Клиент " + clientMessage.getName() + " зарегистрировался.");
            logger.info("Принято сообщение от клиента: " + clientMessage.getMessage());

            System.out.println("Сообщение клиента: " + clientMessage.getMessage());
            return true;
        } else {
            System.out.println("Не удалось обработать входящее сообщение.");
            return false;
        }
    }

    private void processOutputMessage(Boolean isProcessInputSuccessful) {
        String messageForClient;
        if (isProcessInputSuccessful) {
            messageForClient = String.format("Добрый день, %s, Ваше сообщение успешно обработано!",
                    clientMessage.getName());
        } else {
            messageForClient = "Ваше сообщение не удалось обработать успешно";
        }
        String answer = XMLMessageWriter.makeXmlMessageInString(messageForClient, true);
        MessageUtils.sendMessage(clientSocket, answer);
    }

    private void closeClient() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
                logger.info("Клиент закрыт");
            } catch (IOException e) {
                logger.error("Не смогу закрыть клиента. " + e);
            }
        }
    }
}


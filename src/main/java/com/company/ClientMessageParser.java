package com.company;
/*Класс разбирает сообщение клиента, при необходимости сохраняет сообщение в файл.
 * Для работы с xml используется DOM */

import com.common.XmlFileSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.StringReader;

public class ClientMessageParser {
    private static ClientMassage clientMessage = new ClientMassage();
    private static DocumentBuilder dBuilder;
    private static Document doc;
    private static final Logger logger = LogManager.getLogger(ClientMessageParser.class);

    /*Единственный доступный извне для вызова метод, возращает извлеченное из xml сообщение клиента
    в виде объекта типа ClientMassage*/
    public static ClientMassage parseClientMessage(String messageXml, Boolean needAdditionallyFile) {
        makeBuilder();
        parseMessageToDoc(messageXml);
        createClientMessage();
        if (needAdditionallyFile) {
            XmlFileSaver.makeXmlMessageInFile("ClientMessage.xml", doc);
        }
        return clientMessage;
    }

    private static void makeBuilder() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException pEx) {
            logger.error("Невозможно построить билдер: " + pEx);
        }
    }

    private static void parseMessageToDoc(String messageXml) {
        try {
            doc = dBuilder.parse(new InputSource(new StringReader(messageXml)));
            doc.getDocumentElement().normalize();
        } catch (Exception iEx) {
            logger.error("Невозможно разобрать xml сообщение: " + iEx);
        }

    }

    private static void createClientMessage() {

        NodeList nodeList = doc.getElementsByTagName("user");

        if (nodeList.getLength() != 0) {
            Node node = nodeList.item(0);
            Element eUser = (Element) node;

            NodeList nodeMessage = eUser.getElementsByTagName("message");
            NodeList nodeName = eUser.getElementsByTagName("name");

            if (nodeMessage.getLength() != 0 && nodeName.getLength() != 0) {
                String message = nodeMessage.item(0).getTextContent();
                String name = nodeName.item(0).getTextContent();

                clientMessage.setName(name);
                clientMessage.setMessage(message);
            } else {
                logger.warn("В сообщении клиента не удалось найти теги name или message");
            }
        } else {
            logger.warn("В сообщении клиента нет блока user");
        }
    }


}





package com.company;
/*Класс создает ответное сообщение в виде xml, возращаемое в строке, при необходимости сохраняет сообщение в файл.
* Для работы с xml используется DOM */
import com.common.XmlFileSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLMessageWriter {
    private static Document doc;
    private static Transformer transformer;
    private static final Logger logger = LogManager.getLogger(XMLMessageWriter.class);
    private static StringWriter stw;

    /*Единственный доступный извне для вызова метод. Поочередное вызывает необходимые для создания xml
     * методы класса - создание пустого документа, заполнение, преобразование в строку.*/
    public static String makeXmlMessageInString(String textForMessage, Boolean needAdditionallyFile) {
        makeEmptyDoc();
        fillDoc(textForMessage);
        transformToString();
        if (needAdditionallyFile) {
            XmlFileSaver.makeXmlMessageInFile("ServerMessage.xml", doc);
        }
        return stw.toString();
    }

    private static void makeEmptyDoc() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);
        } catch (ParserConfigurationException pEx) {
            logger.error("Невозможно построить билдер: " + pEx);
        }
    }

    private static void fillDoc(String textForMessage) {
        Element rootElement = doc.createElement("response");
        doc.appendChild(rootElement);

        rootElement.appendChild(createElement("message",textForMessage ));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date dateTime = new Date();
        rootElement.appendChild(createElement("date",dateFormat.format(dateTime) ));
    }

    private static Element createElement (String tagName, String tagText){
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(tagText));
        return element;
    }

    private static void transformToString() {
        makeTransformer();
        try {
            stw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(stw));
        } catch (TransformerException tEx) {
            logger.error("Невозможно сформировать ответное сообщение: " + tEx);

        }
    }

    private static void makeTransformer() {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException trEx) {
            logger.error("Невозможно построить трансформер: " + trEx);
        }

    }
}

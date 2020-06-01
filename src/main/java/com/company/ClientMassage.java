package com.company;
/*Класс хранит используемые из сообщения клиента поля*/

public class ClientMassage {
    private String name;
    private String message;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

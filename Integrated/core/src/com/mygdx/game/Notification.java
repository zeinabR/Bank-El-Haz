package com.mygdx.game;

public class Notification {
    public enum NotificationType {
        Buy,
        ReadOnly
    }

    float timeout = 20;
    String text = "";
    int price = 0;
    NotificationType notificationType = NotificationType.Buy;
    int playerID;

    public Notification(int price, String city, float timeout) {
        this.price = price;
        this.text = city;
        this.timeout = timeout;
        this.notificationType = NotificationType.Buy;
    }

    public Notification(String text, float timeout) {
        this.text = text;
        this.timeout = timeout;
        this.notificationType = NotificationType.ReadOnly;
    }
}

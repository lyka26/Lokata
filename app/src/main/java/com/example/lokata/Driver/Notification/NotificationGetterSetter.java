package com.example.lokata.Driver.Notification;

public class NotificationGetterSetter {
    String notifDetail, notifDate, notifImage;

    public NotificationGetterSetter() {}

    public NotificationGetterSetter(String notifDetail, String notifImage, String notifDate) {
        this.notifDetail = notifDetail;
        this.notifDate = notifDate;
        this.notifImage = notifImage;
    }

    public String getNotifDetail() {
        return notifDetail;
    }

    public void setNotifDetail(String notifDetail) {
        this.notifDetail = notifDetail;
    }

    public String getNotifDate() {
        return notifDate;
    }

    public void setNotifDate(String notifDate) {
        this.notifDate = notifDate;
    }

    public String getNotifImage() {
        return notifImage;
    }

    public void setNotifImage(String notifImage) {
        this.notifImage = notifImage;
    }
}

package com.dcinspirations.bookstore;

public class NotificationDetails {
    final int paymentSuccessfulNotificationId = 1;
    final String paymentSuccessfulNotificationChannelId = "payment_successful_channel";
    final String paymentSuccessfulNotificationChannelName = "Payments";
    final String paymentSuccessfulNotificationChannelDescription = "This channel alerts the user of successful payments";
    final String paymentSuccessfulNotificationTitle = "Payment successful";

    final int orderSuccessfulNotificationId = 2;
    final String orderSuccessfulNotificationChannelId = "order_successful_channel";
    final String orderSuccessfulNotificationChannelName = "Orders";
    final String orderSuccessfulNotificationChannelDescription = "This channel alerts the user of successful orders";
    final String orderSuccessfulNotificationTitle = "Order successful";
    final String orderSuccessfulNotificationBody = "Your order has been booked for delivery";
}

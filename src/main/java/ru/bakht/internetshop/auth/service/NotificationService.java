package ru.bakht.internetshop.auth.service;


import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;

public interface NotificationService {

    void sendEmailForUserAction(String messageRecipient, EmailTemplateName templateType, User user);
}

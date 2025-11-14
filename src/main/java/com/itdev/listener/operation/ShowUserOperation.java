package com.itdev.listener.operation;

import com.itdev.dao.entity.User;
import com.itdev.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowUserOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final UserService userService;

    public ShowUserOperation(UserService userService) {
        this.userService = userService;
        this.consoleOperationType = ConsoleOperationType.SHOW_ALL_USERS;
    }

    @Override
    public void execute() {
        List<User> users = userService.findAllWithAccounts();
        if (users.isEmpty()) {
            System.out.println("There are no users yet.");
        } else {
            StringBuilder stringBuilder = new StringBuilder("List of all users: ");
            users.forEach(obj -> stringBuilder.append(System.lineSeparator()).append(obj));
            System.out.println(stringBuilder);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}

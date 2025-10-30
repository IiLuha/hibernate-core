package com.itdev.listener.operation;

import java.util.Scanner;

public interface OperationCommand {

    void execute(Scanner scanner);

    ConsoleOperationType getOperationType();
}

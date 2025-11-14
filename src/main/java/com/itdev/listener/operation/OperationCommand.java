package com.itdev.listener.operation;

public interface OperationCommand {

    void execute();

    ConsoleOperationType getOperationType();
}

package com.itdev.listener.operation;

public enum ConsoleOperationType {
    ACCOUNT_CREATE("create"),
    SHOW_ALL_USERS("show"),
    ACCOUNT_CLOSE("close"),
    ACCOUNT_WITHDRAW("withdraw"),
    ACCOUNT_DEPOSIT("deposit"),
    ACCOUNT_TRANSFER("transfer"),
    USER_CREATE("create");

    private final String operationName;

    ConsoleOperationType(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }
}

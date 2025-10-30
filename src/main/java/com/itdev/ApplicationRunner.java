package com.itdev;

import com.itdev.listener.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationRunner {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.itdev");
        OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
        listener.doListen();
    }
}

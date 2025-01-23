package org.example.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandInvoker {
    private final List<CartCommand> commandHistory = new ArrayList<>();

    public void executeCommand(CartCommand command) {
        command.execute();
        commandHistory.add(command);
    }
}
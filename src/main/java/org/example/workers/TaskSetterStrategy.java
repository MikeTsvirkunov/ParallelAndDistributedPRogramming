package org.example.workers;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.concurrent.Callable;

public class TaskSetterStrategy implements IStrategy {

    @Override
    public Object execute(Object... args) {
        AbstractList<Callable<Object>>  listOfTasks = IoC.caster.cast(args);
        AbstractQueue<Callable<Object>> taskQueue = IoC.resolve("Variables.TaskQueue");
        taskQueue.addAll(listOfTasks);
        return null;
    }
}

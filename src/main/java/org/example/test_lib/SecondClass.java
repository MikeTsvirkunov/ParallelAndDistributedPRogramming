package org.example.test_lib;

import org.example.test_lib.interfaces.ISecondClass;

public class SecondClass implements ISecondClass {
    @Override
    public void secondClassMethod() {
        System.out.println("SecondClass method called");
    }
}

package org.example.test_lib;

import org.example.test_lib.interfaces.IFirstClass;
import org.example.test_lib.interfaces.ISecondClass;

public class FirstSecondExtendedByThirdClass extends ThirdClass implements IFirstClass, ISecondClass {
    @Override
    public void firstClassMethod() {
        System.out.println("FirstExtendedBySecondClass: First class method");
    }

    @Override
    public void secondClassMethod() {
        System.out.println("FirstExtendedBySecondClass: Second class method");
    }
}

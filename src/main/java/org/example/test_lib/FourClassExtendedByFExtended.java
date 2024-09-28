package org.example.test_lib;

import org.example.test_lib.interfaces.IFourClass;

public class FourClassExtendedByFExtended extends FirstSecondExtendedByThirdClass implements IFourClass {
    @Override
    public void fourClassMethod() {
        System.out.println("fourClassMethod");
    }
}

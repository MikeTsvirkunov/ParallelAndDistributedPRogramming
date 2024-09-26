package org.example.inversion_of_control.datatype_caters;

import org.example.interfaces.ICaster;
import org.example.inversion_of_control.IoC;

public class DefaultDataTypeCaster implements ICaster {
    @Override
    public <T> T cast(Object obj) {
        return (T) obj;
    }
}

package org.example.datatype_caster;

import org.example.interfaces.ICaster;

public class DataTypeCaster implements ICaster {
    @Override
    public <T> T cast(Object obj) {
        return ((T) obj);
    }
}

package org.example.interfaces;

public interface IScope {
    Object resolve(String key, Object... args) throws Exception;
}

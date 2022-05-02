package com.thana.sugarapi.common.utils.config;

public interface ConfigEnum<E> {

    E parse(String text);

    E next(E value);

    E previous(E value);

    String save(E value);
}

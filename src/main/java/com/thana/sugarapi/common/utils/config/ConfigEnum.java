package com.thana.sugarapi.common.utils.config;

public interface ConfigEnum<E> {

    E parse(String text);

    E next();

    E previous();

    String save();
}

package com.vicktoriyasin.translator.engine;

public enum EngineType {
    GOOGLE(0, "Google"),
    YANDEX(1, "Yandex");

    public final int id;
    public final String name;

    EngineType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

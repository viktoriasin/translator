package com.vicktoriyasin.translator.util;

import java.time.LocalDateTime;

public class Log {
    public static void info(String msg) {
        System.out.println(now() + msg);
    }

    public static void error(String msg) {
        System.err.println(now() + msg);
    }

    public static void error(Throwable throwable) {
        error(throwable.toString());
        throwable.printStackTrace();
    }

    private static String now() {
        return LocalDateTime.now() + ": ";
    }
}

package com.example.hotel_booking.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static ExecutorService IO;
    public static ExecutorService io() {
        if (IO == null) IO = Executors.newSingleThreadExecutor();
        return IO;
    }
}

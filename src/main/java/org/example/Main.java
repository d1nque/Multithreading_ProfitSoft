package org.example;

import org.example.model.ModifiedVersion;
import org.example.model.Util;
import org.example.model.Utility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.zip.DataFormatException;

public class Main {
    public static void main(String[] args){

    }
}

/*
    2 threads took: 1482ms

    4 threads took: 1161ms

    8 threads took: 661ms

    Without parallelization: 3535ms
 */
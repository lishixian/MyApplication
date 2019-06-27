package com.example.myapplication;

public class NdkJniUtils {
    static {
        System.loadLibrary("MyLibrary");
    }
    public native String getCLanguageString();
}

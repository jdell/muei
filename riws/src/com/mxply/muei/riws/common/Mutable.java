package com.mxply.muei.riws.common;

public class Mutable<T> {
    private T value;
    public Mutable() { this.value = null; }
    public Mutable(T value) { this.value = value; }
    public T get() { return this.value; }
    public void set(T value) { this.value = value; }
}
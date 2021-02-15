package com.wang.spring.mvc;

import java.lang.reflect.Method;
import java.util.Objects;

public class Handler {
    protected Object controller;
    protected Method method;

    public Handler(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Handler handler = (Handler) o;
        return Objects.equals(controller, handler.controller) &&
                Objects.equals(method, handler.method);
    }

    @Override
    public int hashCode() {

        return Objects.hash(controller, method);
    }
}

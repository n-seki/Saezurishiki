package com.seki.saezurishiki.control;

public interface Consumer<T> {
        void accept(T t);
}
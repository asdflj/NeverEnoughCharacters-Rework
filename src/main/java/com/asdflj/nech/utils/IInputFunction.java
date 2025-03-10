package com.asdflj.nech.utils;

import java.util.function.Function;

@FunctionalInterface
public interface IInputFunction<T, R> extends Function<T, R>, IIndexFunction {
}

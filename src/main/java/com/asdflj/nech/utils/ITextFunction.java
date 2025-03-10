package com.asdflj.nech.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ITextFunction<T, R> extends Function<T, R>, IIndexFunction {

}

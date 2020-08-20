package com.example.demo.operators;

import io.vavr.control.Either;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static io.vavr.API.*;

public class EitherExtensions {
    public static <R> Function<Throwable, Either<String, R>> throwableMessage() {
        return x -> Either.left(x.getMessage());
    }

    public static <L, T, T1> Function<Either<L, T>, Either<L, T1>> mapT(Function<T, T1> fn) {
        return $source -> $source.map(fn);
    }

    public static <L, T, T1> Function<Either<L, T>, CompletableFuture<Either<L, T1>>> bindT(Function<T, CompletableFuture<Either<L, T1>>> fn) {
        return $source -> $source.fold(
                error -> CompletableFuture.completedFuture(Either.left(error)),
                value -> fn.apply(value));
    }


}

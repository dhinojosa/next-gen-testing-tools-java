package com.evolutionnext.awaitility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

public class AwaitilityTest {

    private static ExecutorService executorService;
    private Future<Integer> integerFuture;

    @BeforeAll
    static void beforeAll() {
        executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
    }

    @BeforeEach
    void setUp() {
        integerFuture = executorService.submit(() -> {
            try {
                Thread.sleep(2000);
                return 30;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testBasicFutureWithoutAwaitility() throws ExecutionException,
        InterruptedException {
        Integer integer = integerFuture.get();
        assertThat(integer).isEqualTo(30);
    }

    @Test
    void testBasicFutureWithAwaitilityAndHamcrest() {
        await().until(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return integerFuture.get();
            }
        }, equalTo(30));
    }

    @Test
    void testBasicFutureWithAwaitilityAndBoolean() {
        await().until(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return integerFuture.get();
            }
        }, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 30;
            }
        });
    }

    @Test
    void testBasicFutureWithAwaitilityAndAConditionEvaluator() {
        await().until(() -> integerFuture.get() == 30);
    }

    @Test
    void testBasicFutureWithAwaitilityAndForcingAssertJ() {
        await().untilAsserted(() -> assertThat(integerFuture.get()).isEqualTo(30));
    }
}

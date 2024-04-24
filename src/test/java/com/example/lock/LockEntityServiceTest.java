package com.example.lock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LockEntityServiceTest {
    @Autowired
    private LockService lockService;

    @Autowired
    private LockFacade lockFacade;

    @Autowired
    private LockRepository lockRepository;

    private final int taskCount = 32;
    private LockEntity testLockEntity;

    private void executeParallel(int taskCount, Runnable runnable) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    @BeforeEach
    void setUp() {
        testLockEntity = lockRepository.save(
                new LockEntity(0, "")
        );
    }

    @AfterEach
    void tearDown() {
        lockRepository.deleteAll();
    }

    @Test
    void updateCount() throws InterruptedException {
        this.executeParallel(this.taskCount,
                () -> lockService.updateCount(testLockEntity.getId(), 1));

        LockEntity lockEntity = lockRepository.findById(testLockEntity.getId()).orElseThrow();
        assertNotEquals(this.taskCount, lockEntity.getCount());
    }

    @Test
    void updateCountWithPessimisticLock() throws InterruptedException {
        this.executeParallel(this.taskCount,
                () -> lockService.updateCountWithPessimisticLock(testLockEntity.getId(), 1));

        LockEntity lockEntity = lockRepository.findById(testLockEntity.getId()).orElseThrow();
        assertEquals(this.taskCount, lockEntity.getCount());
    }

    @Test
    void updateCountWithOptimisticLock() throws InterruptedException {
        this.executeParallel(this.taskCount,
                () -> {
                    try {
                        lockFacade.updateCountWithOptimisticLock(testLockEntity.getId(), 1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        LockEntity lockEntity = lockRepository.findById(testLockEntity.getId()).orElseThrow();
        assertEquals(this.taskCount, lockEntity.getCount());
    }

    @Test
    void updateCountWithNamedLock() throws InterruptedException {
        this.executeParallel(this.taskCount,
                () -> lockFacade.updateCountWithNamedLock(testLockEntity.getId(), 1));

        LockEntity lockEntity = lockRepository.findById(testLockEntity.getId()).orElseThrow();
        assertEquals(this.taskCount, lockEntity.getCount());
    }

    @Test
    void updateCountAtomic() throws InterruptedException {
        this.executeParallel(this.taskCount,
                () -> lockService.updateCountAtomic(testLockEntity.getId(), 1));

        LockEntity lockEntity = lockRepository.findById(testLockEntity.getId()).orElseThrow();
        assertEquals(this.taskCount, lockEntity.getCount());
    }
}
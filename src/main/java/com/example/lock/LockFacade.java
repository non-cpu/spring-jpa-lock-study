package com.example.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockFacade {
    private final LockService lockService;
    private final LockRepository lockRepository;

    @Transactional(propagation = Propagation.NEVER)
    public void updateCountWithOptimisticLock(Long lockId, int incrementValue) throws InterruptedException {
        while (true) {
            try {
                lockService.updateCountWithOptimisticLock(lockId, incrementValue);

                break;
            } catch (Exception e) {
                Thread.sleep(4);
            }
        }
    }

    @Transactional
    public void updateCountWithNamedLock(Long lockId, int incrementValue) {
        try {
            lockRepository.getLock(lockId.toString());
            lockService.updateCount(lockId, incrementValue);
        } finally {
            lockRepository.releaseLock(lockId.toString());
        }
    }
}

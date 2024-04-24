package com.example.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockService {
    private final LockRepository lockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCount(Long lockId, int incrementValue) {
        LockEntity lockEntity = lockRepository.findById(lockId).orElseThrow();
        lockEntity.updateCount(incrementValue);
    }

    @Transactional
    public void updateCountWithPessimisticLock(Long lockId, int incrementValue) {
        LockEntity lockEntity = lockRepository.findByIdWithPessimisticLock(lockId);
        lockEntity.updateCount(incrementValue);
    }

    @Transactional
    public void updateCountWithOptimisticLock(Long lockId, int incrementValue) {
        LockEntity lockEntity = lockRepository.findByIdWithOptimisticLock(lockId);
        lockEntity.updateCount(incrementValue);
    }

    @Transactional
    public void updateCountAtomic(Long lockId, int incrementValue) {
        lockRepository.updateCountAtomic(lockId, incrementValue);
    }
}

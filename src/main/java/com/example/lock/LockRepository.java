package com.example.lock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LockRepository extends JpaRepository<LockEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LockEntity l where l.id = :id")
    LockEntity findByIdWithPessimisticLock(Long id);


    @Lock(LockModeType.OPTIMISTIC)
    @Query("select l from LockEntity l where l.id = :id")
    LockEntity findByIdWithOptimisticLock(Long id);


    @Query(value = "select get_lock(:key, 2)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);


    @Modifying
    @Query(value = "update LockEntity l set l.count = l.count + :incrementValue where l.id = :id")
    void updateCountAtomic(Long id, int incrementValue);
}

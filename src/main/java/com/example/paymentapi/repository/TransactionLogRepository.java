
package com.example.paymentapi.repository;

import com.example.paymentapi.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    boolean existsByReference(String reference);
    Optional<TransactionLog> findByReference(String reference);
}

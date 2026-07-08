package com.kalvium.library.repository;

import com.kalvium.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    int countByBorrowerIdAndStatus(String borrowerId, Loan.LoanStatus status);
    List<Loan> findByStatusAndDueDateBefore(Loan.LoanStatus status, java.time.LocalDate date);
}

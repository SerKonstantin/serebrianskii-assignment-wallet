package org.assignment.wallet.repository;


import org.assignment.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query(value = "SELECT * FROM wallets WHERE id = :walletId FOR UPDATE", nativeQuery = true)
    Optional<Wallet> findByIdForUpdate(@Param("walletId") UUID walletId);

}

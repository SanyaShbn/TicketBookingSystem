package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.UserCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCartRepository extends JpaRepository<UserCart, Long> {

    @Query("FROM UserCart uc WHERE uc.id.userId = :userId")
    List<UserCart> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserCart uc WHERE uc.id.userId = :userId")
    void deleteByUserId(Long userId);
}
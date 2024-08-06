package com.dao.bot.repository;

import com.dao.bot.entity.LockJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LockJobRepository extends JpaRepository<LockJob, String> {

    Optional<LockJob> findByNameAndIsLock(String name, Boolean isLock);
}

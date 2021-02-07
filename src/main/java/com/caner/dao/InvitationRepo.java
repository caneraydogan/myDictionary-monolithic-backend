package com.caner.dao;

import com.caner.bean.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepo extends JpaRepository<Invitation, Long> {
    Invitation findByCode(String code);
}

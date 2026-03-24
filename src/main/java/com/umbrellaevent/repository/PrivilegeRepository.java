package com.umbrellaevent.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {

}

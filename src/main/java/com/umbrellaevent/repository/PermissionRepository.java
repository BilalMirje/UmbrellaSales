package com.umbrellaevent.repository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Permissions;
import com.umbrellaevent.entity.Role;

@Repository
public interface PermissionRepository extends JpaRepository<Permissions, UUID> {
    Collection<Object> getPermissionsByRole(Role savedRole);

    List<Permissions> getPermissionsByRole_Id(UUID roleId);
}

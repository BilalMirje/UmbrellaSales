// package com.umbrellaevent.service;

// import java.util.List;
// import java.util.UUID;

// import com.umbrellaevent.entity.Permissions;
// import com.umbrellaevent.entity.Privilege;
// import com.umbrellaevent.entity.Role;
// import com.umbrellaevent.entity.dtos.auth.RoleDto;

// public interface RoleService {
//     Role createRole(RoleDto roleDto, String username);
//     List<RoleDto> getAllRoles();
//     List<Permissions> createPermissions(List<Permissions> permissions);
//     Privilege createPrivilege(Privilege privilege);

//     RoleDto getRoleByRoleName(String roleName);

//     Role updateRole(RoleDto roleDto, String username) ;

//     boolean deleteRole(UUID roleId);

//     Role findById(UUID roleId);
// }
package com.umbrellaevent.service;


import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.Permissions;
import com.umbrellaevent.entity.Privilege;
import com.umbrellaevent.entity.Role;
import com.umbrellaevent.entity.dtos.auth.RoleDto;

public interface RoleService {
    Role createRole(RoleDto roleDto);
    List<RoleDto> getAllRoles();
    List<Permissions> createPermissions(List<Permissions> permissions);
    Privilege createPrivilege(Privilege privilege);

    RoleDto getRoleByRoleName(String roleName);

    Role updateRole(RoleDto roleDto) ;

    boolean deleteRole(UUID roleId);

    Role findById(UUID roleId);
}

package com.umbrellaevent.controller;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.Role;
import com.umbrellaevent.entity.dtos.auth.RoleDto;
import com.umbrellaevent.entity.dtos.auth.UserResponse;
import com.umbrellaevent.repository.RoleRepository;
import com.umbrellaevent.service.AppUserService;
import com.umbrellaevent.service.RoleService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin("*")
@SuppressWarnings("null")
public class AppUserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final AppUserService appUserService;

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestParam String name,
                                        @RequestParam String address,
                                        @RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam String contact,
                                        @RequestParam String email,
                                        @RequestParam(required = false) LocalDate joinDate,
                                        @RequestParam UUID role_id) throws IOException {
        AppUser user= new AppUser();
        user.setName(name);
        user.setAddress(address);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setContact(contact);
        user.setEmail(email);
        user.setDateOfJoining(joinDate);

//        user.setIsUserLoggedIn(false);
        // user.setIsDeleted(false);
//        user.setIsMultiFactor(false);
        user.setRole(roleRepository.findById(role_id).get());

        AppUser appUser = appUserService.saveUser(user, null);
        if(appUser!=null){
            return ResponseEntity.ok(appUser);
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(@RequestParam UUID id){
        return ResponseEntity.ok(appUserService.getAppUser(id));
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<?> getAllUser(){
        List<UserResponse> allAppUsers = appUserService.getAllAppUsers();
        if(allAppUsers!=null){
            return ResponseEntity.ok(allAppUsers);
        }
        return ResponseEntity.ok(null);
    }

//    @DeleteMapping("/soft-delete-user")
//    public ResponseEntity<?> softDeleteUser(@RequestParam UUID id){
//        return ResponseEntity.ok(appUserService.softDeleteUser(id));
//    }
//    @PutMapping("/retrieve-user")
//    public ResponseEntity<?> retrieveUser(@RequestParam UUID id){
//        return ResponseEntity.ok(appUserService.retrieveUser(id));
//    }
//    @GetMapping("/get-all-deleted-user")
//    public ResponseEntity<?> getDeletedUsers(){
//        List<AppUser> user = appUserService.getAllSoftDeletedUser();
//        if(user!=null){
//            return ResponseEntity.ok(user);
//        }
//        return ResponseEntity.ok(null);
//    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestParam UUID id,
                                        @RequestParam String name,
                                        @RequestParam String address,
                                        @RequestParam String username,
                                        @RequestParam(required = false) String password,
                                        @RequestParam String contact,
                                        @RequestParam String email,
                                        @RequestParam(required = false) LocalDate joinDate,
                                        @RequestParam UUID role_id) throws IOException {

        AppUser appUser = new AppUser();
        appUser.setId(id);
        appUser.setName(name);
        appUser.setAddress(address);
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setContact(contact);
        appUser.setEmail(email);
        if(joinDate!=null){
            appUser.setDateOfJoining(joinDate);
        }

        appUser.setRole(roleRepository.findById(role_id).get());
        AppUser user = appUserService.updateAppUser(appUser, null);
        if(user!=null){
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam UUID id){
        boolean result=appUserService.deleteUserById(id);
        return result?
                ResponseEntity.ok(true):
                ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/create-role")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto){
        Role role = roleService.createRole(roleDto);
        if(role!=null){
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get-all-roles")
    public ResponseEntity<?> getAllRole(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/get-role-by-role-name")
    public ResponseEntity<?> getRole(@RequestParam String roleName){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoleByRoleName(roleName));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    @PutMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.updateRole(roleDto));
    }
    /**
     *    Deletes a role.
     *       @param id The ID of the role to be deleted.
     *       @return {@link ResponseEntity} containing success or failure response.
     *
     */
    @DeleteMapping("/delete-role")
    public ResponseEntity<?> deleteRole(@RequestParam UUID id){
        boolean result= roleService.deleteRole(id);
        return result?
                ResponseEntity.ok(true):
                ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}


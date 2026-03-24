package com.umbrellaevent.service;


import com.google.zxing.WriterException;
import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.dtos.auth.OTPRequest;
import com.umbrellaevent.entity.dtos.auth.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AppUserService {

    AppUser saveUser(AppUser user, MultipartFile imageFile) throws IOException;

    UserResponse getAppUser(UUID id);

    List<UserResponse> getAllAppUsers();

    AppUser updateAppUser(AppUser user,MultipartFile imageFile) throws IOException;

    boolean deleteUserById(UUID userId);


    String enableTwoFactorAuthentication(String username) throws WriterException, IOException;

    String disableTwoFactorAuthentication(String username);

    Boolean verify2FA(OTPRequest otpRequest);



}
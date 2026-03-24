package com.umbrellaevent.service.impl;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.dtos.auth.OTPRequest;
import com.umbrellaevent.entity.dtos.auth.UserResponse;
import com.umbrellaevent.repository.AppUserRepository;
import com.umbrellaevent.service.AppUserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {


    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private static final String ISSUER = "UmbrellaEvent";
    private final GoogleAuthenticator googleAuthenticator=new GoogleAuthenticator();

    @Override
    public AppUser saveUser(AppUser user, MultipartFile file) throws IOException {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public UserResponse getAppUser(UUID id) {
        AppUser user = getSingleUser(id);
        if (user != null) {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setAddress(user.getAddress());
            response.setUsername(user.getUsername());
            response.setPassword(user.getPassword()); // Optional: you may want to exclude this
            response.setContact(user.getContact());
//            response.setIsMultiFactor(user.getIsMultiFactor());
//            response.setIsUserLoggedIn(user.getIsUserLoggedIn());
            // response.setIsDeleted(user.getIsDeleted());
//            response.setCreatedAt(user.getCreatedAt());
//            response.setUpdatedAt(user.getUpdatedAt());
            response.setDateOfJoining(user.getDateOfJoining());
           
            response.setRole(user.getRole());

            return response;
        }
        return null;
    }


    @Override
    public List<UserResponse> getAllAppUsers() {
        List<AppUser> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(AppUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setAddress(user.getAddress());
        response.setUsername(user.getUsername());
        // response.setPassword(user.getPassword()); // Omit for security
        response.setContact(user.getContact());
        response.setEmail(user.getEmail());
        response.setDateOfJoining(user.getDateOfJoining());

//        response.setIsMultiFactor(user.getIsMultiFactor());
//        response.setIsUserLoggedIn(user.getIsUserLoggedIn());
        // response.setIsDeleted(user.getIsDeleted());
//        response.setCreatedAt(user.getCreatedAt());
//        response.setUpdatedAt(user.getUpdatedAt());
        response.setRole(user.getRole());

        return response;
    }


    @Override
    public AppUser updateAppUser(AppUser user, MultipartFile file) throws IOException {
        AppUser appUser = getSingleUser(user.getId());

        if(appUser!=null){
            appUser.setName(user.getName());
            appUser.setContact(user.getContact());
            appUser.setAddress(user.getAddress());
            appUser.setUsername(user.getUsername());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                appUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            appUser.setEmail(user.getEmail());
            appUser.setDateOfJoining(user.getDateOfJoining());

            return userRepository.save(appUser);
        }
        return null;
    }

    @SuppressWarnings("null")
    @Override
    public boolean deleteUserById(UUID userId) {
        Optional<AppUser> userOpt=userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return false;
        }

        if(userOpt.get().getUsername().equals("superadmin.com")){
            return false;
        }

        userRepository.deleteById(userId);
        return true;
    }

    @SuppressWarnings("null")
    @Override
    public String enableTwoFactorAuthentication(String username) throws WriterException, IOException {

        Optional<AppUser> user = userRepository.getMyUserByUsername(username);
        if (user.isEmpty()) {
            return "User Not Found";
        }

        if (user.get().getSecretKey() != null) {
            return "User Already Enabled";
        }

        String generatedKey = generateKey();

        String qrCodeUrl = "otpauth://totp/" + ISSUER + ":" + user.get().getUsername()
                + "?secret=" + generatedKey + "&issuer=" + ISSUER;

        //  QR Code Generation inline
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG", pngOutputStream);
        String base64QR = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());

        //  Set user fields (using user.get())
        user.get().setSecretKey(generatedKey);
        user.get().setUpdatedAt(LocalDateTime.now());
        user.get().setIsMultiFactor(true);
        user.get().setQrCodeImage(base64QR);

        userRepository.save(user.get());

        return "2 Factor Enable"; // Return success message without QR code
    }


    @Override
    public String disableTwoFactorAuthentication(String username) {
        Optional<AppUser> userOpt = userRepository.getMyUserByUsername(username);
        if (userOpt.isEmpty()) {
            return "User Not Found";
        }

        AppUser user = userOpt.get();
        if (!user.getIsMultiFactor()) {
            return "2FA Not Enabled";
        }

        user.setIsMultiFactor(false);
        user.setSecretKey(null);
        user.setQrCodeImage(null);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "2 Factor Disabled";
    }

    @Override
    public Boolean verify2FA(OTPRequest otpRequest) {
        Optional<AppUser> userOpt = userRepository.getMyUserByUsername(otpRequest.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if (!userOpt.get().getIsMultiFactor()) {
            throw new RuntimeException("2FA not enabled for this user");
        }
        String secret = userOpt.get().getSecretKey();
        if (secret == null || secret.isEmpty()) {
            throw new RuntimeException("2FA secret key not found");
        }
        return googleAuthenticator.authorize(secret, Integer.parseInt(otpRequest.getOtp()));
    }

    // 🔹 Generate Secret Key (keep private)
    private String generateKey() {
        GoogleAuthenticatorKey key = new GoogleAuthenticator().createCredentials();
        return key.getKey();
    }



    @SuppressWarnings("null")
    private AppUser getSingleUser(UUID id){
        Optional<AppUser> user = userRepository.findById(id);
        return user.orElse(null);
    }
}

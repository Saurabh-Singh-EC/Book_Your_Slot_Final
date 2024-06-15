package com.codeWithSrb.BookYourSlot.Controller;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.*;
import com.codeWithSrb.BookYourSlot.Repository.BookingRepository;
import com.codeWithSrb.BookYourSlot.Service.BookingService;
import com.codeWithSrb.BookYourSlot.Service.RoleService;
import com.codeWithSrb.BookYourSlot.Service.UserDetailsServiceImpl;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import com.codeWithSrb.BookYourSlot.provider.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper.fromUserInfo;
import static com.codeWithSrb.BookYourSlot.util.ExceptionUtils.processError;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequestMapping("/api/v1/booking")
@Slf4j
public class Controller {

    private static final String TOKEN_PREFIX = "Bearer ";
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
    @Autowired
    BookingService bookingService;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerNewUser(@RequestBody UserInfo userInfo) {
        UserInfoDTO registeredUser = userDetailsServiceImpl.registerNewUser(userInfo);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(CREATED)
                        .withStatusCode(CREATED.value())
                        .withMessage("User Created")
                        .withData(Map.of("user", registeredUser))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid UserLoginForm userLoginForm) {
        Authentication authentication = authenticate(userLoginForm.getEmail(), userLoginForm.getPassword());
        UserDetailsImpl userDetailsImpl = getAuthenticatedUser(authentication);
        UserInfoDTO userInfoDTO = userDetailsImpl.getUser();

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(OK)
                        .withStatusCode(OK.value())
                        .withMessage("Login Successful")
                        .withData(Map.of("user", userInfoDTO
                                , "access_token", tokenProvider.createAccessToken(getUserDetailsImpl(userDetailsImpl.getUserInfo()))
                                ,"refresh_token", tokenProvider.createRefreshToken(getUserDetailsImpl(userDetailsImpl.getUserInfo()))))
                        .build());
    }

    private Authentication authenticate(String email, String password) {
        try {
            log.info("Authenticating the user");
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            log.info("User authenticated successfully");
            return authentication;
        } catch (Exception exception) {
            processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }
    }

    private UserDetailsImpl getAuthenticatedUser(Authentication authentication) {
        return ((UserDetailsImpl)authentication.getPrincipal());
    }

    private UserDetailsImpl getUserDetailsImpl(UserInfo userInfo) {
        return new UserDetailsImpl(userInfo, roleService.getRoleByUserId(userInfo.getId()));
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        UserInfo userInfo = userDetailsServiceImpl.findUserByEmail(authentication.getName());
        UserInfoDTO userInfoDTO = fromUserInfo(userInfo, roleService.getRoleByUserId(userInfo.getId()));

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(OK)
                        .withStatusCode(OK.value())
                        .withMessage("Profile retrieved")
                        .withData(Map.of("user", userInfoDTO))
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {

        if(isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserInfo userInfo = userDetailsServiceImpl.findUserByEmail(tokenProvider.getSubject(token, request));
            UserInfoDTO userInfoDTO = fromUserInfo(userInfo);

            return ResponseEntity.ok()
                    .body(HttpResponse.builder()
                            .withTimeStamp(now().toString())
                            .withHttpStatus(OK)
                            .withStatusCode(OK.value())
                            .withMessage("Token refreshed.")
                            .withData(Map.of("user", userInfoDTO
                                    , "access_token", tokenProvider.createAccessToken(getUserDetailsImpl(userInfo))
                                    ,"refresh_token", token))
                            .build());
        } else {
            return ResponseEntity.ok()
                    .body(HttpResponse.builder()
                            .withTimeStamp(now().toString())
                            .withHttpStatus(BAD_REQUEST)
                            .withStatusCode(BAD_REQUEST.value())
                            .withReason("Refresh token missing or invalid.")
                            .withDevelopersMessage("Refresh token missing or invalid.")
                            .build());
        }
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request)
                ,request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }

    @RequestMapping(value = "/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(NOT_FOUND)
                        .withStatusCode(NOT_FOUND.value())
                        .withReason("There is no mapping for a " + request.getMethod() + " on the server")
                        .build());
    }

    @PostMapping("/book")
    public ResponseEntity<HttpResponse> bookSlot(@RequestBody BookingForm bookingForm) {
        UserInfo userInfo = userDetailsServiceImpl.findUserByEmail(bookingForm.getEmail());
        if(ObjectUtils.isEmpty(userInfo)) {
            return ResponseEntity.badRequest()
                    .body(HttpResponse.builder()
                            .withTimeStamp(now().toString())
                            .withHttpStatus(BAD_REQUEST)
                            .withStatusCode(BAD_REQUEST.value())
                            .withMessage("Enter the correct email id for booking.")
                            .build());
        }

        bookingService.createNewBooking(bookingForm, userInfo);
        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(OK)
                        .withStatusCode(OK.value())
                        .withMessage("Booking is successful")
                        .build());
    }
}
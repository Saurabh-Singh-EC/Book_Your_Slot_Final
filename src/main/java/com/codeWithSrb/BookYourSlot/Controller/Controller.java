package com.codeWithSrb.BookYourSlot.Controller;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.BookingForm;
import com.codeWithSrb.BookYourSlot.Model.HttpResponse;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Model.UserLoginForm;
import com.codeWithSrb.BookYourSlot.Service.BookingService;
import com.codeWithSrb.BookYourSlot.Service.RoleService;
import com.codeWithSrb.BookYourSlot.Service.UserInfoService;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import com.codeWithSrb.BookYourSlot.dto.UserInfoRegisterDTO;
import com.codeWithSrb.BookYourSlot.provider.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final BookingService bookingService;
    private final UserInfoService userInfoService;

    public Controller(RoleService roleService,
                      AuthenticationManager authenticationManager,
                      TokenProvider tokenProvider,
                      HttpServletRequest request,
                      HttpServletResponse response,
                      BookingService bookingService,
                      UserInfoService userInfoService) {
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.request = request;
        this.response = response;
        this.bookingService = bookingService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerNewUser(@RequestBody UserInfoRegisterDTO userInfoRegisterDTO) {
        UserInfoDTO registeredUser = userInfoService.registerNewUser(userInfoRegisterDTO);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(CREATED)
                        .statusCode(CREATED.value())
                        .message("User Created successfully")
                        .data(Map.of("user", registeredUser))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid UserLoginForm userLoginForm) {
        Authentication authentication = authenticate(userLoginForm.getEmail(), userLoginForm.getPassword());
        UserDetailsImpl userDetailsImpl = getAuthenticatedUser(authentication);
        UserInfoDTO userInfoDTO = userDetailsImpl.getUser();

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Login Successful")
                        .data(Map.of("user", userInfoDTO
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
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        UserInfoDTO userInfoDTO = fromUserInfo(userInfo, roleService.getRoleByUserId(userInfo.getId()));

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Profile retrieved")
                        .data(Map.of("user", userInfoDTO))
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {

        if(isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserInfo userInfo = userInfoService.findUserByEmail(tokenProvider.getSubject(token, request));
            UserInfoDTO userInfoDTO = fromUserInfo(userInfo);

            return ResponseEntity.ok()
                    .body(HttpResponse.builder()
                            .timeStamp(now().toString())
                            .httpStatus(OK)
                            .statusCode(OK.value())
                            .message("Token refreshed.")
                            .data(Map.of("user", userInfoDTO
                                    , "access_token", tokenProvider.createAccessToken(getUserDetailsImpl(userInfo))
                                    ,"refresh_token", token))
                            .build());
        } else {
            return ResponseEntity.ok()
                    .body(HttpResponse.builder()
                            .timeStamp(now().toString())
                            .httpStatus(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .reason("Refresh token missing or invalid.")
                            .developerMessage("Refresh token missing or invalid.")
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
                        .timeStamp(now().toString())
                        .httpStatus(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .reason("There is no mapping for a " + request.getMethod() + " on the server")
                        .build());
    }

    @PostMapping("/book")
    public ResponseEntity<HttpResponse> bookSlot(@RequestBody BookingForm bookingForm) {
        UserInfo userInfo = userInfoService.findUserByEmail(bookingForm.getEmail());
        if(ObjectUtils.isEmpty(userInfo)) {
            return ResponseEntity.badRequest()
                    .body(HttpResponse.builder()
                            .timeStamp(now().toString())
                            .httpStatus(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .message("Enter the correct email id for booking.")
                            .build());
        }

        bookingService.createNewBooking(bookingForm, userInfo);
        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Booking is successful")
                        .build());
    }
}
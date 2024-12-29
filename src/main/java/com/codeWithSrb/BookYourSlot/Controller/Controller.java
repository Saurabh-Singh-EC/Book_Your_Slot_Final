package com.codeWithSrb.BookYourSlot.Controller;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.*;
import com.codeWithSrb.BookYourSlot.Service.AuthenticatorImpl;
import com.codeWithSrb.BookYourSlot.Service.BookingService;
import com.codeWithSrb.BookYourSlot.Service.UserInfoService;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import com.codeWithSrb.BookYourSlot.dto.ResetPasswordDTO;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import com.codeWithSrb.BookYourSlot.dto.UserInfoRegisterDTO;
import com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper;
import com.codeWithSrb.BookYourSlot.provider.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper.fromUserInfo;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/booking")
@Slf4j
public class Controller {

    private final TokenProvider tokenProvider;
    private final BookingService bookingService;
    private final UserInfoService userInfoService;
    private final AuthenticatorImpl authenticatorImpl;

    private static final String RESET_PASSWORD_MESSAGE = "If the email provided is linked to an account, you'll receive a password reset link shortly. Please check your inbox and spam folder.";

    public Controller(TokenProvider tokenProvider,
                      BookingService bookingService,
                      UserInfoService userInfoService,
                      AuthenticatorImpl authenticatorImpl) {
        this.tokenProvider = tokenProvider;
        this.bookingService = bookingService;
        this.userInfoService = userInfoService;
        this.authenticatorImpl = authenticatorImpl;
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
        Authentication authentication = authenticatorImpl.authenticate(userLoginForm.getEmail(), userLoginForm.getPassword());
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();

        UserInfoDTO userInfoDTO = userDetailsImpl.getUser();

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Login Successful")
                        .data(Map.of("user", userInfoDTO
                                , "access_token", tokenProvider.createAccessToken(userDetailsImpl)
                                ,"refresh_token", tokenProvider.createRefreshToken(userDetailsImpl)))
                        .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        UserInfoDTO userInfoDTO = fromUserInfo(userDetailsImpl.getUserInfo(), userDetailsImpl.getRole());

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Profile retrieved")
                        .data(Map.of("user", userInfoDTO))
                        .build());
    }

    //Start - Reset password when user is not logged in

    @PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        Optional<UserInfo> userInfoOptional = userInfoService.findUserByEmail(resetPasswordDTO.getEmail());

        userInfoOptional.ifPresent(userInfoService::generateResetPasswordLink);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message(RESET_PASSWORD_MESSAGE)
                        .build());
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyResetPassword(@PathVariable String key) {
        UserInfo userInfo = userInfoService.verifyPasswordKey(key);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Please reset your password.")
                        .data(Map.of("user", UserDTOMapper.fromUserInfo(userInfo)))
                        .build());
    }

    @PostMapping("/renew-password/key")
    public ResponseEntity<HttpResponse> resetNewPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        userInfoService.renewPassword(passwordResetRequest);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Your password has been successfully updated. Please log in using your new password.")
                        .build());
    }

    //End - Reset password when user is not logged in

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
        UserInfo userInfo = retrieveUserInfo(bookingForm.getEmail());
        if (ObjectUtils.isEmpty(userInfo)) {
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

    private UserInfo retrieveUserInfo(String email) {
        Optional<UserInfo> userInfo = userInfoService.findUserByEmail(email);

        return userInfo.orElseThrow(() -> new ApiException(String.format("User with name: %s does not exist", email)));
    }
}
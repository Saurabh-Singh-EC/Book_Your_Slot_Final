package com.codeWithSrb.bookyourslot.controller;

import com.codeWithSrb.bookyourslot.model.HttpResponse;
import com.codeWithSrb.bookyourslot.dto.ResetNotLoggedInUserPasswordDTO;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.model.UserLoginForm;
import com.codeWithSrb.bookyourslot.service.AuthenticatorImpl;
import com.codeWithSrb.bookyourslot.service.ResponseService;
import com.codeWithSrb.bookyourslot.config.UserDetailsImpl;
import com.codeWithSrb.bookyourslot.dto.ResetLoggedInUserPasswordRequestDTO;
import com.codeWithSrb.bookyourslot.dto.ResetPasswordRequestDTO;
import com.codeWithSrb.bookyourslot.dto.UserInfoDTO;
import com.codeWithSrb.bookyourslot.dto.UserInfoRegisterDTO;
import com.codeWithSrb.bookyourslot.dtomapper.UserDTOMapper;
import com.codeWithSrb.bookyourslot.provider.TokenProvider;
import com.codeWithSrb.bookyourslot.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.codeWithSrb.bookyourslot.dtomapper.UserDTOMapper.fromUserInfo;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/booking")
@Slf4j
public class UserController {

    private final TokenProvider tokenProvider;
    private final UserInfoService userInfoService;
    private final AuthenticatorImpl authenticatorImpl;
    private final ResponseService responseService;

    private static final String RESET_PASSWORD_MESSAGE = "If the email provided is linked to an account, you'll receive a password reset link shortly. Please check your inbox and spam folder.";
    private static final String RENEW_USER_PASSWORD_MESSAGE = "Password change successfully. Please use new password to login.";


    public UserController(TokenProvider tokenProvider,
                          UserInfoService userInfoService,
                          AuthenticatorImpl authenticatorImpl,
                          ResponseService responseService) {
        this.tokenProvider = tokenProvider;
        this.userInfoService = userInfoService;
        this.authenticatorImpl = authenticatorImpl;
        this.responseService = responseService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerNewUser(@RequestBody @Valid UserInfoRegisterDTO userInfoRegisterDTO) {
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

    @GetMapping("/user/profile")
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

    //Start - Reset password when user is logged in

    @PostMapping("/user/reset-password")
    public ResponseEntity<HttpResponse> resetLoggedInUserPassword(@RequestBody @Valid ResetLoggedInUserPasswordRequestDTO resetLoggedInUserPasswordRequestDTO, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        userInfoService.renewLoggedInUserPassword(resetLoggedInUserPasswordRequestDTO, userDetailsImpl.getUserInfo());

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message(RENEW_USER_PASSWORD_MESSAGE)
                        .build());
    }

    //End - Reset password when user is logged in

    //Start - Reset password when user is not logged in

    @PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> resetNotLoggedInUserPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Optional<UserInfo> userInfoOptional = userInfoService.findUserByEmail(resetPasswordRequestDTO.getEmail());

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

    @PostMapping("/reset-password/key")
    public ResponseEntity<HttpResponse> resetNewPassword(@RequestBody ResetNotLoggedInUserPasswordDTO resetNotLoggedInUserPasswordDTO) {
        userInfoService.renewNotLoggedInUserPassword(resetNotLoggedInUserPasswordDTO);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message(RENEW_USER_PASSWORD_MESSAGE)
                        .build());
    }

    //End - Reset password when user is not logged in

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .reason("There is no mapping for a " + request.getMethod() + " on the server")
                        .build());
    }
}
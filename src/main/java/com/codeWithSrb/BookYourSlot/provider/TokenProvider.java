package com.codeWithSrb.BookYourSlot.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codeWithSrb.BookYourSlot.Service.UserDetailsServiceImpl;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES = "authorities";
    private static final String CODE_WITH_SRB_LLC = "CODE_WITH_SRB_LLC";
    private static final String BOOKING_MANAGEMENT_SERVICE = "BOOKING_MANAGEMENT_SERVICE";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

    @Value("&{jwt.secret}")
    private String secret;

    private final UserDetailsServiceImpl userDetailsService;

    public TokenProvider(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(UserDetailsImpl userDetailsImpl) {
        String[] claims = getClaimsFromUser(userDetailsImpl);
        return JWT.create()
                .withIssuer(CODE_WITH_SRB_LLC)
                .withAudience(BOOKING_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userDetailsImpl.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserDetailsImpl userDetailsImpl) {
        return JWT.create()
                .withIssuer(CODE_WITH_SRB_LLC)
                .withAudience(BOOKING_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userDetailsImpl.getUsername())
                .withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    private static String[] getClaimsFromUser(UserDetailsImpl userDetailsImpl) {
        return userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    public List<GrantedAuthority> getGrantedAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        stream(claims).forEach(System.out::println);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }
    public Authentication getAuthentication(String email, List<GrantedAuthority> grantedAuthorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(userDetailsService.loadUserByUsername(email), null, grantedAuthorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isSubjectAndTokenValid(String email, String token,  HttpServletRequest request) {
        return StringUtils.isNotEmpty(email) && !isTokenExpired(token, request);
    }

    private boolean isTokenExpired(String token, HttpServletRequest request) {
        return verifyToken(token, request).getExpiresAt().before(new Date());
    }

    public String getSubject(String token, HttpServletRequest request) {
        return verifyToken(token, request).getSubject();
    }

    private DecodedJWT verifyToken(String token, HttpServletRequest request) {
        try {
            return getJwtVerifier().verify(token);
        }  catch (IllegalArgumentException exception) {
            request.setAttribute("invalidArgument", exception.getMessage());
            throw exception;
        } catch (JWTDecodeException exception) {
            request.setAttribute("invalidJwt", exception.getMessage());
            throw exception;
        } catch (SignatureVerificationException exception) {
            request.setAttribute("invalidSignature", exception.getMessage());
            throw exception;
        }  catch (AlgorithmMismatchException exception) {
            request.setAttribute("algorithmMismatch", exception.getMessage());
            throw exception;
        } catch (TokenExpiredException exception) {
            request.setAttribute("tokenExpired", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch(JWTVerificationException exception) {
            request.setAttribute("jwtVerification", exception.getMessage());
            throw exception;
        }
    }

    private JWTVerifier getJwtVerifier() {
        Algorithm algorithm = HMAC512(secret);
        return JWT.require(algorithm).withIssuer(CODE_WITH_SRB_LLC).build();
    }
}

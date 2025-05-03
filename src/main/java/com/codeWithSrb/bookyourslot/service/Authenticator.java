package com.codeWithSrb.bookyourslot.service;

import org.springframework.security.core.Authentication;

public interface Authenticator {

    Authentication authenticate(String name, String credential);
}

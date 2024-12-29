package com.codeWithSrb.BookYourSlot.Service;

import org.springframework.security.core.Authentication;

public interface Authenticator {

    Authentication authenticate(String name, String credential);
}

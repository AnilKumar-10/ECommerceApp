package com.ECommerceApp.Util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class OwnershipGuard {

    public void checkSelf(String ownerId) {
        String currentUserId = new SecurityUtils().getCurrentUserId();
        if (!currentUserId.equals(ownerId)) {
            throw new AccessDeniedException("Access denied: not your data.");
        }
    }

    public void checkSelfOrThrow(boolean condition) {
        if (!condition) {
            throw new AccessDeniedException("Access denied: not authorized.");
        }
    }
}
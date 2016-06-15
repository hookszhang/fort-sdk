package com.boyuanitsm.fort.sdk.util;

import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.*;

import java.util.List;
import java.util.Set;

/**
 * Utility class for Fort Security.
 *
 * @author zhanghua on 5/19/16.
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static String getCurrentUserLogin() {
        SecurityUser user = getSecurityUser();

        if (user == null) {
            return null;
        }

        return user.getLogin();
    }

    /**
     * If the current user has a specific role.
     *
     * @param roleName the name of the SecurityRole
     * @return true if the current user has the role, false otherwise
     */
    public static boolean isCurrentUserInRole(String roleName) {
        SecurityUser user = getSecurityUser();

        if (user == null) {
            return false;
        }

        Set<SecurityRole> roleSet = user.getRoles();

        for (SecurityRole role: roleSet) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the login of the current user roles.
     *
     * @return the login of the current user roles.
     */
    public static Set<SecurityRole> getCurrentUserRoles() {
        SecurityUser user = getSecurityUser();

        if (user == null) {
            return null;
        }

        return user.getRoles();
    }

    /**
     * Get the login of the current user groups.
     *
     * @return the login of the current user groups.
     */
    public static Set<SecurityGroup> getCurrentUserGroups() {
        SecurityUser user = getSecurityUser();

        if (user == null) {
            return null;
        }

        return user.getGroups();
    }

    /**
     * Get the login of the current user authorities.
     *
     * @return the login of the current user authorities.
     */
    public static Set<SecurityAuthority> getCurrentUserAuthorities() {
        if (FortContextHolder.getContext() == null) {
            return null;
        }
        return FortContextHolder.getContext().getAuthorities();
    }

    /**
     * Get the login of the current user tree security navs.
     *
     * @return the login of the current user tree security navs.
     */
    public static List<TreeSecurityNav> getCurrentUserTreeSecurityNavs() {
        if (FortContextHolder.getContext() == null) {
            return null;
        }
        return FortContextHolder.getContext().getNavs();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user;
     */
    private static SecurityUser getSecurityUser() {
        if (FortContextHolder.getContext() == null) {
            return null;
        }
        return FortContextHolder.getContext().getSecurityUser();
    }
}

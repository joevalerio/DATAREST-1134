package org.springframework.sdr.services;

import org.springframework.sdr.model.BaseEntity;
import org.springframework.sdr.repositories.SecuredRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityService {

    public static final String readPermission       = "@securityService.hasReadPermission(#this.getThis())";
    public static final String editPermission       = "@securityService.hasEditPermission(#this.getThis())";
    public static final String editMinePermission   = "@securityService.hasEditMinePermission(#this.getThis(), #entity)";
    public static final String editMinePermissionId = "@securityService.hasEditMinePermissionId(#this.getThis(), #entityId)";

    private static final String ROLE = "ROLE_";
    private static final String ADMIN = "_ADMIN";
    private static final String READ_WRITE = "_READ_RIGHT";
    private static final String READ_ONLY = "_READ_ONLY";
    private static final String SYSTEM = "ROLE_SYSTEM";

    public boolean hasReadPermission(SecuredRepository<?> repo) {
        log.debug("hasReadPermission called");
        String type = repo.getType();
        return hasAnyRole(makeRole(type, ADMIN), makeRole(type, READ_WRITE), makeRole(type, READ_ONLY), SYSTEM);
    }

    public boolean hasEditPermission(SecuredRepository<?> repo) {
        log.debug("hasEditPermission called");
        String type = repo.getType();
        return hasAnyRole( makeRole(type, ADMIN), makeRole(type, READ_WRITE), SYSTEM);
    }

    public boolean hasEditMinePermission(SecuredRepository<?> repo, BaseEntity entity) {
        log.debug("hasEditMinePermission called");
        String type = repo.getType();
        return hasAnyRole( makeRole(type, ADMIN), makeRole(type, READ_WRITE), SYSTEM)
            || (   hasAnyRole( makeRole(type, READ_ONLY))
                && ( entity.getCreatedBy() == null || getUserDetails().getUsername().equals(entity.getCreatedBy()) )
               );
    }

    public boolean hasEditMinePermissionId(SecuredRepository<?> repo, String entityId) {
        log.debug("hasEditMinePermissionId called");
        BaseEntity entity = repo.findOne(entityId);
        return hasEditMinePermission(repo, entity);
    }

    private String makeRole(String type, String role) {
        return ROLE + type + role;
    }

    public boolean hasAnyRole(String... roles) {
        for (String role:roles) {
            if (hasRole(role)) return true;
        }

        return false;
    }

    public boolean hasRole(String role) {
        if (role == null)
            return false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ((auth != null) && auth.isAuthenticated()) {
            for (GrantedAuthority ga : auth.getAuthorities())
                if (role.equalsIgnoreCase(ga.getAuthority()))
                    return true;
        }

        return false;
    }

    public UserDetails getUserDetails() {
        UserDetails user = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ((auth != null) && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                user = (UserDetails) principal;
            } else {
                log.warn("UserDetails NOT in Session");
            }
        }

        return user;
    }

}

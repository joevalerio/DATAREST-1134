package org.springframework.sdr.eventhandlers;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.sdr.model.Bar;
import org.springframework.sdr.model.BaseEntity;
import org.springframework.sdr.model.Foo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CreatedByEventHandler {

    @HandleBeforeCreate
    public void addCreatedBy(Foo foo){
        addInternalCreatedBy(foo);
    }

    @HandleBeforeCreate
    public void addCreatedBy(Bar bar){
        addInternalCreatedBy(bar);
    }

    protected void addInternalCreatedBy(BaseEntity entity){
        String username = getCurrentAuditor();
        entity.setCreatedBy(username);
    }

    protected String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "wtf";
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}

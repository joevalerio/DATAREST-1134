package org.springframework.sdr.repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.sdr.model.Bar;
import org.springframework.sdr.services.SecurityService;
import org.springframework.security.access.prepost.PreAuthorize;

public interface BarRepository extends SecuredRepository<Bar>{

    @Override
    default String getType() {
        return "BAR";
    }

    @Override
    @PreAuthorize(SecurityService.editMinePermission)
    <T extends Bar> T save(@Param("entity") T entity);

}

package org.springframework.sdr.repositories;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.sdr.model.BaseEntity;
import org.springframework.sdr.services.SecurityService;
import org.springframework.security.access.prepost.PreAuthorize;

@NoRepositoryBean
@PreAuthorize(SecurityService.readPermission)
public interface SecuredRepository<T extends BaseEntity> extends PagingAndSortingRepository<T, String>{

    default String getType(){
        return "VOID";
    };

    /***********************************
     * Read Mine Permissible methods
     ***********************************/
    @Override
    @PreAuthorize(SecurityService.editMinePermission)
    <S extends T> S save( @Param("entity")S entity);

    @Override
    @PreAuthorize(SecurityService.editMinePermission)
    void delete( @Param("entity") T entity);

    @Override
    @PreAuthorize(SecurityService.editMinePermissionId)
    void delete( @Param("entityId") String entityId);

    /***********************************
     * Edit Permissible methods
     ***********************************/

    @Override
    @PreAuthorize(SecurityService.editPermission)
    <S extends T> List<S> save(Iterable<S> entities);

    @Override
    @PreAuthorize(SecurityService.editPermission)
    void delete(Iterable<? extends T> entities);

    @Override
    @PreAuthorize(SecurityService.editPermission)
    void deleteAll();

}

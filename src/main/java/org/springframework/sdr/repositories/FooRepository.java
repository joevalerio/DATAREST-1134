package org.springframework.sdr.repositories;

import org.springframework.sdr.model.Foo;

public interface FooRepository extends SecuredRepository<Foo> {

    @Override
    default String getType() {
        return "FOO";
    }


}

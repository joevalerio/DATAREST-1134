package org.springframework.sdr;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.sdr.model.Foo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FooSecurityTest extends SecurityTest {

    @Test
    public void test1FooAdminCrudOwn() throws Exception {
        Foo foo = new Foo(ADMIN, DESC);
        testAdminCrudOwn(foo);
    }

    @Test
    public void test2FooAdminRudOthers() throws Exception {
        Foo foo = new Foo(EDITOR, DESC);
        testAdminRudOthers(foo);
    }

    @Test
    public void test3FooReaderCrudOwn() throws Exception {
        Foo foo = new Foo(READER, DESC);
        testReaderCrudOwn(foo);
    }

    @Test
    public void test4FooReaderCantRudOthers() throws Exception {
        Foo foo = new Foo(EDITOR, DESC);
        testReaderCantRudOthers(foo);
    }

}

package org.springframework.sdr;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.sdr.model.Bar;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarSecurityTest extends SecurityTest {

    @Test
    public void test1BarAdminCrudOwn() throws Exception {
        Bar bar = new Bar(ADMIN, DESC);
        testAdminCrudOwn(bar);
    }

    @Test
    public void test2BarAdminRudOthers() throws Exception {
        Bar bar = new Bar(EDITOR, DESC);
        testAdminRudOthers(bar);
    }

    @Test
    public void test3BarReaderCrudOwn() throws Exception {
        Bar bar = new Bar(READER, DESC);
        testReaderCrudOwn(bar);
    }

    @Test
    public void test4BarReaderCantRudOthers() throws Exception {
        Bar bar = new Bar(EDITOR, DESC);
        testReaderCantRudOthers(bar);
    }

}

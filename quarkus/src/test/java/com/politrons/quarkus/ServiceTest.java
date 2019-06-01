package com.politrons.quarkus;

import com.politrons.quarkus.service.PolitronsQuarkusService;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    @Test
    public void testServ(){
        PolitronsQuarkusService politronsQuarkusService = new PolitronsQuarkusService();
        assert(politronsQuarkusService.hashCode() > 0);
    }

}

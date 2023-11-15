package gov.cabinetoffice.gap.spotlight.publisher.lambda;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

class HandlerTest {

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler();


    }



    @Test
    void testHandleRequestWithMessages() {
        final Map<String, Object> event = Map.of("Records", new ArrayList<>());
    }

    @Test
    void testHandleRequestWithNoMessages() {
        Map<String, Object> sqsEvent = Map.of("Records", new ArrayList<>());
    }


}


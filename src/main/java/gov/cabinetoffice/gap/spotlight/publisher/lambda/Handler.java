package gov.cabinetoffice.gap.spotlight.publisher.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler {
    @Override
    public Object handleRequest(Object o, Context context) {
        System.out.println("delete me");
        return null;
    }
}

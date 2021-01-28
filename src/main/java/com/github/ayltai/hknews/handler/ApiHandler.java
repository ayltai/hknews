package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public abstract class ApiHandler extends Handler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
}

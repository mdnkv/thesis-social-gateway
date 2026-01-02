package dev.mednikov.social.gateway.web;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class CurrentUserWebHandler implements Handler<RoutingContext> {

    private final WebClient webClient;

    public CurrentUserWebHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void handle(RoutingContext context) {
        this.webClient.getAbs("http://localhost:8001/users/current")
                .putHeaders(context.request().headers())
                .as(BodyCodec.jsonObject())
                .send()
                .onComplete(result -> {
                    if (result.succeeded()) {
                        JsonObject payload = result.result().body();
                        context.response()
                                .setStatusCode(200)
                                .end(payload.encode());
                    } else {
                        context.fail(result.cause());
                    }
                });
    }
}

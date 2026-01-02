package dev.mednikov.social.gateway.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public final class WebVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(WebVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        WebClient webClient = WebClient.create(vertx);
        router.route().handler(CorsHandler.create()
                .allowCredentials(true)
                .allowedMethods(Set.of(HttpMethod.GET, HttpMethod.OPTIONS))
                .allowedHeaders(Set.of("Authorization", "Content-Type", "Access-Control-Allow-Origin"))
        );
        router.get("/users/current").handler(new CurrentUserWebHandler(webClient));
        server.requestHandler(router);
        server.listen(config().getInteger("APP_HTTP_PORT", 8000)).onComplete(result -> {
            if (result.succeeded()) {
                logger.info("Web server is started on port {}", result.result().actualPort());
                startPromise.complete();
            } else {
                logger.error("Failed to start web server", result.cause());
                startPromise.fail(result.cause());
            }
        });
    }
}

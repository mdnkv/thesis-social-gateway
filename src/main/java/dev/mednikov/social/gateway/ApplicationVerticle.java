package dev.mednikov.social.gateway;

import dev.mednikov.social.gateway.web.WebVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig().map(result -> {
            WebVerticle webVerticle = new WebVerticle();
            return vertx.deployVerticle(webVerticle, new DeploymentOptions().setConfig(result));
        }).onComplete(result -> {
            if (result.succeeded()) {
                logger.info("Application verticle was started");
                startPromise.complete();
            } else {
                logger.error("Application verticle was unable to start");
                startPromise.fail(result.cause());
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ApplicationVerticle appVerticle = new ApplicationVerticle();
        vertx.deployVerticle(appVerticle).onComplete(result -> {
            if (result.succeeded()) {
                logger.info("Application started");
            } else {
                logger.error("Application failed to start", result.cause());
            }
        });
    }

}

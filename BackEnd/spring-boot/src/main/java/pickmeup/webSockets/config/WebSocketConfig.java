package pickmeup.webSockets.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import pickmeup.webSockets.config.CustomConfigurator;

/**
 * The type Web socket config.
 */
@ConditionalOnWebApplication
@Configuration
public class WebSocketConfig {

    /**
     * Server endpoint exporter server endpoint exporter.
     *
     * @return the server endpoint exporter
     */
    @Bean
public ServerEndpointExporter serverEndpointExporter() {
return new ServerEndpointExporter();
}

    /**
     * Custom configurator custom configurator.
     *
     * @return the custom configurator
     */
    @Bean
public CustomConfigurator customConfigurator() {
return new CustomConfigurator();
}
}

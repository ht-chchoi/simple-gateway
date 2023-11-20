package com.ht.simplegateway

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.util.UriComponentsBuilder
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.SslProvider
import java.util.Optional

@Configuration
class MainRouter {
    @Bean
    fun mainRoute(routeLocatorBuilder: RouteLocatorBuilder): RouteLocator {
        return routeLocatorBuilder.routes()
            .route { predicateSpec ->
                var uriComponentsBuilder: UriComponentsBuilder? = UriComponentsBuilder.fromUriString("http://localhost")
                predicateSpec
                    .path("/**")
                    .filters {gatewayFilterSpec ->
                        gatewayFilterSpec.changeRequestUri {
                            uriComponentsBuilder = UriComponentsBuilder
                                .fromHttpRequest(it.request)
                                .scheme("https")
                            if (it.request.queryParams.containsKey("targetIp")) {
                                uriComponentsBuilder!!
                                    .host(it.request.queryParams.getFirst("targetIp"))
                            }
                            if (it.request.queryParams.containsKey("targetPort")) {
                                uriComponentsBuilder!!
                                    .port(it.request.queryParams.getFirst("targetPort"))
                            }
                            Optional.of(uriComponentsBuilder!!.build().toUri())
                        }
                    }.uri(uriComponentsBuilder?.toUriString())
            }
            .build()
    }

    @Bean
    @Primary
    fun httpClient(): HttpClient {
        val sslProvider = SslProvider.builder().sslContext(
            SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE))
            .defaultConfiguration(SslProvider.DefaultConfigurationType.NONE).build()
        return HttpClient.create().secure(sslProvider)
    }
}
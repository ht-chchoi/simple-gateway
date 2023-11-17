package com.ht.simplegateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleGatewayApplication

fun main(args: Array<String>) {
    runApplication<SimpleGatewayApplication>(*args)
}

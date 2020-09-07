package me.jerry.example.webmvc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinMvcApplication

fun main(args: Array<String>) {
	runApplication<KotlinMvcApplication>(*args)
}

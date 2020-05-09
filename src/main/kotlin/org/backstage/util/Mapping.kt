package org.backstage.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.inject.Singleton

val objectMapper: ObjectMapper = ObjectMapper().configure()

@Singleton
class ConfigureJacksonObjectMapper : ObjectMapperCustomizer {
    override fun customize(objectMapper: ObjectMapper?) {
        objectMapper?.configure()
    }
}

fun ObjectMapper.configure(): ObjectMapper = apply {
    findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

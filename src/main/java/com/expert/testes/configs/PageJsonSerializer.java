package com.expert.testes.configs;

import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.data.domain.Page;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Essa classe é um serializador personalizado para objetos do tipo Page<?>, usada para converter objetos de paginação
 * em um formato JSON específico. Basicamente essa Classe personaliza como objetos de paginação são convertidos para JSON
 **/

@JacksonComponent
public class PageJsonSerializer extends ValueSerializer<Page<?>> {


    @Override
    public void serialize(Page<?> page, JsonGenerator gen, SerializationContext serializers) throws JacksonException {


        gen.writeStartObject(); // Inicia a escrita do objeto JSON.

        gen.writePOJOProperty("content", page.getContent());
        gen.writeNumberProperty("size", page.getSize());
        gen.writeNumberProperty("totalElements", page.getTotalElements());
        gen.writeNumberProperty("totalPages", page.getTotalPages());
        gen.writeNumberProperty("number", page.getNumber());

        gen.writeEndObject(); // Finaliza a escrita do objeto JSON.
    }

}


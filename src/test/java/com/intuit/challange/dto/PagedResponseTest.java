package com.intuit.challange.dto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PagedResponseTest {

    @Test
    @DisplayName("Estructura - Verificación de contenido y metadatos")
    void testPagedStructure() {
        PagedResponse.PageMetadata meta = PagedResponse.PageMetadata.builder()
                .number(0).size(10).totalElements(100).build();

        PagedResponse<String> response = PagedResponse.<String>builder()
                .content(List.of("Elemento 1"))
                .page(meta)
                .build();

        assertNotNull(response.getContent());
        assertEquals(10, response.getPage().getSize());
        assertEquals(0, response.getPage().getNumber());
    }
}

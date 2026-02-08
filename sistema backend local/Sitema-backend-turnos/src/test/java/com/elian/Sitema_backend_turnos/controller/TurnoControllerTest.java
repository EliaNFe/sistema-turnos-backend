package com.elian.Sitema_backend_turnos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaActualizarEstadoDelTurno() throws Exception {

        // JSON del request
        String body = """
            {
                "estado": "CONFIRMADO"
            }
            """;

        mockMvc.perform(
                        patch("/turnos/1/estado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDO"));
    }

    @Test
    void noDeberiaActualizarConEstadoInvalido() throws Exception {

        String body = """
        {
            "estado": "CUALQUIERA"
        }
        """;

        mockMvc.perform(
                        patch("/turnos/1/estado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void noDebeCrearTurnoConFormatoFechaInvalido() throws Exception {

        String json = """
                {
                  "fecha": "2024-99-99",
                  "clienteId": 1
                }
                """;

        mockMvc.perform(
                        post("/turnos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "clienteId": 1,
                                              "profesionalId": 1,
                                              "fecha": "2024-99-99",
                                              "hora": "25:61"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());  //Test passed !! no deja crear turno con fecha invalida
    }
}

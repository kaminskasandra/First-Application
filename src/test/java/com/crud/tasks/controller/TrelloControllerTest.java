package com.crud.tasks.controller;


import com.crud.tasks.domain.CreatedTrelloCardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.trello.facade.TrelloFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrelloController.class)
public class TrelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrelloFacade trelloFacade;

    @Test
    public void createTrelloCardTest() throws Exception {

        //Given
        TrelloCardDto trelloCardDto = new TrelloCardDto();
        trelloCardDto.setName("Test Card");

        CreatedTrelloCardDto createdTrelloCardDto = new CreatedTrelloCardDto();
        createdTrelloCardDto.setId("1");
        createdTrelloCardDto.setName("Test Card");

        when(trelloFacade.createCard(trelloCardDto)).thenReturn(createdTrelloCardDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/trello/cards")
                        .content("{\"name\":\"Test Card\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Card"));

        verify(trelloFacade, times(1)).createCard(trelloCardDto);
    }

    @Test
    public void testGetTrelloBoards() throws Exception {

        //Given
        when(trelloFacade.fetchTrelloBoards()).thenReturn(Collections.emptyList());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/trello/boards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(trelloFacade, times(1)).fetchTrelloBoards();
    }

    @Test
    public void testGetEmptyTrelloBoards() throws Exception {

        //Given
        when(trelloFacade.fetchTrelloBoards()).thenReturn(Collections.emptyList());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/trello/boards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

        verify(trelloFacade, times(1)).fetchTrelloBoards();
    }
}


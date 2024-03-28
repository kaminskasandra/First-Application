package com.crud.tasks.controller;

import com.crud.tasks.domain.Task;
import com.crud.tasks.domain.TaskDto;
import com.crud.tasks.mapper.TaskMapper;
import com.crud.tasks.service.DbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DbService dbService;

    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateTask() throws Exception {

        //Given
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        Task task = new Task();
        task.setId(1L);

        when(taskMapper.mapToTask(taskDto)).thenReturn(task);
        when(dbService.saveTask(task)).thenReturn(task);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetTask() throws Exception {

        //Given
        Long taskId = 1L;
        Task task = new Task(taskId, "Test", "Content");
        TaskDto taskDto = new TaskDto(taskId, "Test", "Content");
        when(dbService.getTask(taskId)).thenReturn(task);
        when(taskMapper.mapToTaskDto(task)).thenReturn(taskDto);

        //When & Then
        mockMvc.perform(get("/v1/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    @Test
    public void testGetAllTasks() throws Exception {

        //Given
        when(dbService.getAllTasks()).thenReturn(Collections.emptyList());

        //When & Then
        mockMvc.perform(get("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(taskMapper, times(1)).mapToTaskDtoList(anyList());
    }

    @Test
    public void testDeleteTask() throws Exception {

        //Given
        TaskDto taskDto = new TaskDto(1L, "Test", "Test task");

        doNothing().when(dbService).deleteTask(taskDto.getId());

        //When & Then
        mockMvc.perform(delete("/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(dbService, times(1)).deleteTask(taskDto.getId());
        verifyNoMoreInteractions(dbService);
    }

    @Test
    public void testEmptyListOfTasks() throws Exception {
        //Given
        List<TaskDto> taskDtoList = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        when(dbService.getAllTasks()).thenReturn(taskList);
        when(taskMapper.mapToTaskDtoList(taskList)).thenReturn(taskDtoList);

        //When & Then
        mockMvc.perform(get("/v1/tasks").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testUpdateTask() throws Exception {
        //Given
        Task task = new Task();
        task.setId(1L);
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);

        when(dbService.saveTask(any())).thenReturn(task);
        when(taskMapper.mapToTaskDto(any())).thenReturn(taskDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
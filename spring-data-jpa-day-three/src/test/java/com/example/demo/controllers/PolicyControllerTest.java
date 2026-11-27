package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.PolicyDto;
import com.example.demo.enums.PolicyType;
import com.example.demo.services.PolicyService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolicyService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PolicyDto policyDto;

    @BeforeEach
    void setUp() {
        policyDto = new PolicyDto(1001L, "Karthik", PolicyType.TermLife, 15000.0);
    }

    @Test
    void testAddPolicy_Success() throws Exception {
        when(service.add(any(PolicyDto.class))).thenReturn(policyDto);

        mockMvc.perform(post("/api/v1/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyDto)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.policyNumber").value(1001L))
                .andExpect(jsonPath("$.policyHolder").value("Karthik"))
                .andExpect(jsonPath("$.premiumAmount").value(15000.0));

        verify(service, times(1)).add(any(PolicyDto.class));
    }

    @Test
    void testAddPolicy_ValidationFailure() throws Exception {
        PolicyDto invalidDto = new PolicyDto(1002L, "AB", PolicyType.TermLife, 15000.0);

        mockMvc.perform(post("/api/v1/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); 
            }

    @Test
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(policyDto));

        mockMvc.perform(get("/api/v1/policy"))
                .andExpect(status().isOk()) // Expects 200
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].policyHolder").value("Karthik"));

        verify(service, times(1)).findAll();
    }

    @Test
    void testFindByName() throws Exception {
        when(service.findByPolicyHolder("Karthik")).thenReturn(List.of(policyDto));

        mockMvc.perform(get("/api/v1/policy/{name}", "Karthik"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].policyNumber").value(1001L))
                .andExpect(jsonPath("$[0].policyHolder").value("Karthik"));

        verify(service, times(1)).findByPolicyHolder("Karthik");
    }

    @Test
    void testGetByPolicyAmount() throws Exception {
        when(service.findByPremium()).thenReturn(List.of(policyDto));

        mockMvc.perform(get("/api/v1/policy/srch/amount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].premiumAmount").value(15000.0));

        verify(service, times(1)).findByPremium();
    }

    @Test
    void testRemoveById() throws Exception {
        doNothing().when(service).remove(1001L);

        mockMvc.perform(delete("/api/v1/policy/{id}", 1001L))
                .andExpect(status().isNoContent()); 
            }
}

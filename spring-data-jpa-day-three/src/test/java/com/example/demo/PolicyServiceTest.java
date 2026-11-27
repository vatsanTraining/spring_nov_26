package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.PolicyDto;
import com.example.demo.enums.PolicyType;
import com.example.demo.ifaces.PolicyRepoistory;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;
import com.example.demo.services.PolicyService;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepoistory repo;

    @Mock
    private PolicyMapper mapper;

    @InjectMocks
    private PolicyService policyService;

    private Policy policy;
    private PolicyDto policyDto;

    @BeforeEach
    void setUp() {
        policy = new Policy(1001L, "Karthik", PolicyType.TermLife, 15000.0);
        policyDto = new PolicyDto(1001L, "Karthik", PolicyType.TermLife, 15000.0);
    }

    @Test
    void testAddPolicy() {
        when(mapper.toEntity(policyDto)).thenReturn(policy);
        when(repo.save(policy)).thenReturn(policy);
        when(mapper.toDto(policy)).thenReturn(policyDto);

        PolicyDto result = policyService.add(policyDto);

        assertNotNull(result);
        assertEquals(1001L, result.policyNumber());
        assertEquals("Karthik", result.policyHolder());
        assertEquals(PolicyType.TermLife, result.policyType());
        assertEquals(15000.0, result.premiumAmount());
        verify(repo, times(1)).save(policy);
    }

    @Test
    void testFindAll() {
        when(repo.findAll()).thenReturn(List.of(policy));
        when(mapper.toDto(policy)).thenReturn(policyDto);

        List<PolicyDto> result = policyService.findAll();

        assertEquals(1, result.size());
        assertEquals("Karthik", result.get(0).policyHolder());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testFindAll_WhenEmpty() {
    
    	when(repo.findAll()).thenReturn(Collections.emptyList());

        List<PolicyDto> result = policyService.findAll();

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testFindByPolicyHolder() {
 
    	String holderName = "Karthik";
        when(repo.findByPolicyHolder(holderName)).thenReturn(List.of(policy));
        when(mapper.toDto(policy)).thenReturn(policyDto);

        List<PolicyDto> result = policyService.findByPolicyHolder(holderName);

        assertEquals(1, result.size());
        assertEquals("Mr.Karthik", "Mr."+result.get(0).policyHolder());
        verify(repo, times(1)).findByPolicyHolder(holderName);
    }

    @Test
    void testFindByPremium() {
    
    	when(repo.getMeByPremiumAmount()).thenReturn(List.of(policy));
        when(mapper.toDto(policy)).thenReturn(policyDto);

        List<PolicyDto> result = policyService.findByPremium();

        assertEquals(1, result.size());
        assertEquals(15000.0, result.get(0).premiumAmount());
        verify(repo, times(1)).getMeByPremiumAmount();
    }

    @Test
    void testRemovePolicy() {
        long policyId = 1001L;
        doNothing().when(repo).deleteById(policyId);

        policyService.remove(policyId);

        verify(repo, times(1)).deleteById(policyId);
    }
}

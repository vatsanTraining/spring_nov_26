package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.dto.PolicyDto;
import com.example.demo.enums.PolicyType;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;

import jakarta.persistence.QueryTimeoutException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class BatchComponentsUnitTest {

    @Mock
    private Validator validator;

    @Mock
    private PolicyMapper mapper;

    
    @Autowired
    private CompositeItemProcessor<PolicyDto, Policy> processor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void processor_validDto_mapsToEntity() throws Exception {
        PolicyDto dto = new PolicyDto(1001L, "Ananand",PolicyType.Endowment ,250.00);
        Policy expectedEntity = new Policy();
        expectedEntity.setPolicyNumber(1001L);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(mapper.toEntity(dto)).thenReturn(expectedEntity);

        Policy result = processor.process(dto);

        assertNotNull(result);
        assertEquals(1001L, result.getPolicyNumber());
        verify(mapper).toEntity(dto);
    }

    @Test
    void processor_duplicatePolicyNumber_returnsNullToFilter() throws Exception {
        PolicyDto dto1 = new PolicyDto(1001L, "Anand",PolicyType.Endowment ,250.00);
 PolicyDto dto2 = new PolicyDto(1001L, "Anand",PolicyType.Endowment ,350.00);

		 
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(mapper.toEntity(dto1)).thenReturn(new Policy());

        assertNotNull(processor.process(dto1)); 
                assertNull(processor.process(dto2));    
                  }

    @Test
    void processor_validationFailure_throwsIllegalArgumentException() {
        PolicyDto invalidDto = new PolicyDto(1002L, "",PolicyType.TermLife ,-10.00);

        @SuppressWarnings("unchecked")
        ConstraintViolation<PolicyDto> violation = mock(ConstraintViolation.class);
        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn("premiumAmount");
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(violation.getMessage()).thenReturn("must be greater than 0");

        when(validator.validate(invalidDto)).thenReturn(Set.of(violation));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> processor.process(invalidDto)
        );

        assertTrue(ex.getMessage().contains("Validation failed for policy 1002"));
        assertTrue(ex.getMessage().contains("premiumAmount: must be greater than 0"));
        verifyNoInteractions(mapper);
    }

    @Test
    void processor_policy1003_throwsTwiceThenSucceeds() throws Exception {
        PolicyDto dto1003 = new PolicyDto(1003L, "Babu", PolicyType.Medical,5000.00);
        Policy expectedEntity = new Policy();
        expectedEntity.setPolicyNumber(1003L);

        when(validator.validate(dto1003)).thenReturn(Collections.emptySet());
        when(mapper.toEntity(dto1003)).thenReturn(expectedEntity);

        assertThrows(QueryTimeoutException.class, () -> processor.process(dto1003));
        assertThrows(QueryTimeoutException.class, () -> processor.process(dto1003));

        Policy result = processor.process(dto1003);
        assertNotNull(result);
        assertEquals(1003L, result.getPolicyNumber());
    }

   
   
}

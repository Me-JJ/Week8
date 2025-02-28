package com.Testing.services.impl;

import com.Testing.TestContainerConfiguration;
import com.Testing.dto.EmployeeDto;
import com.Testing.entities.Employee;
import com.Testing.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest
{
    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockedEmp;
    private EmployeeDto mockerEmpDto;

    @BeforeEach
    void setUp()
    {
        mockedEmp = Employee.builder()
                .id(1L)
                .email("jat@gmail.com")
                .name("jat")
                .salary(100L)
                .build();

        mockerEmpDto=modelMapper.map(mockedEmp,EmployeeDto.class);
    }
    @Test
    void testGetEmpById_WhenEmpIdIsPresent_ThenReturnEmp()
    {
//      assign
        when(employeeRepository.findById(mockedEmp.getId())).thenReturn(Optional.of(mockedEmp)); //stubbing

//      act
        EmployeeDto employeeDto = employeeService.getEmployeeById(mockedEmp.getId());

//      assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(mockedEmp.getId());
        assertThat(employeeDto.getEmail()).isEqualTo(mockedEmp.getEmail());
        verify(employeeRepository,times(1)).findById(1L);
    }


    @Test
    void createNewEmployee_WhenEmpIsValid_ThenSaveTheEmp()
    {
        //assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockedEmp);

        //act
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockerEmpDto);
        //assert

        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockerEmpDto.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmp = employeeArgumentCaptor.getValue();

        assertThat(capturedEmp.getEmail()).isEqualTo(mockedEmp.getEmail());

    }

}


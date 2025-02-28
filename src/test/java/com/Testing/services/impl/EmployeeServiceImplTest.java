package com.Testing.services.impl;

import com.Testing.TestContainerConfiguration;
import com.Testing.dto.EmployeeDto;
import com.Testing.entities.Employee;
import com.Testing.exceptions.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.*;
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
    void testGetEmpById_WhenEmpIdIsNotPresent_ThenThrowException()
    {
//        assign
        when(employeeRepository.findById(any())).thenReturn(Optional.empty());
//        act & assert
        assertThatThrownBy(()-> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
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

    @Test
    void createNewEmployee_WhenEmpIsInvalid_ThenThrowException()
    {
        //assign
        when(employeeRepository.findByEmail(mockerEmpDto.getEmail())).thenReturn(List.of(mockedEmp));
        //act
        assertThatThrownBy(()-> employeeService.createNewEmployee(mockerEmpDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockerEmpDto.getEmail());
        //assert

        verify(employeeRepository).findByEmail(mockedEmp.getEmail());
        verify(employeeRepository,never()).save(any());
    }


    @Test
    void testUpdateEmp_WhenEmpDoesNotExists_thenThrowException()
    {
        when(employeeRepository.findById(mockedEmp.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(()-> employeeService.updateEmployee(mockerEmpDto.getId(),mockerEmpDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found with id: "+ mockerEmpDto.getId());

        verify(employeeRepository).findById(mockedEmp.getId());
        verify(employeeRepository,never()).save(any());
    }


    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException()
    {
        when(employeeRepository.findById(mockerEmpDto.getId())).thenReturn(Optional.of(mockedEmp));
        mockerEmpDto.setName("Random");
        mockerEmpDto.setEmail("random@gmail.com");

//        act and assert

        assertThatThrownBy(() -> employeeService.updateEmployee(mockerEmpDto.getId(), mockerEmpDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockerEmpDto.getId());
        verify(employeeRepository, never()).save(any());
    }


    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee()
    {
        //assign
        when(employeeRepository.findById(mockerEmpDto.getId())).thenReturn(Optional.of(mockedEmp));
        mockerEmpDto.setName("jatin");
        mockerEmpDto.setSalary(1000000L);

        Employee newEmp = modelMapper.map(mockerEmpDto,Employee.class);
        when(employeeRepository.save(any())).thenReturn(newEmp);

        //act
        EmployeeDto employeeDto = employeeService.updateEmployee(mockerEmpDto
                .getId(),mockerEmpDto);
        //assert

        assertThat(employeeDto).isEqualTo(mockerEmpDto);
        verify(employeeRepository).findById(mockerEmpDto.getId());
        verify(employeeRepository).save(any());

    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException()
    {
//        assign
        when(employeeRepository.existsById(mockerEmpDto.getId())).thenReturn(false);
//        act && assert
        assertThatThrownBy(()-> employeeService.deleteEmployee(mockerEmpDto.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + mockerEmpDto.getId());
        verify(employeeRepository, never()).deleteById(anyLong());

    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesExists_thenDeleteEmp()
    {
//        assign
        when(employeeRepository.existsById(mockerEmpDto.getId())).thenReturn(true);
//        act && assert
        assertThatCode(() -> employeeService.deleteEmployee(mockerEmpDto.getId()))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(mockerEmpDto.getId());
    }






}


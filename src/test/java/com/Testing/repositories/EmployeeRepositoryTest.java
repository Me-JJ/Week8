package com.Testing.repositories;

import com.Testing.TestContainerConfiguration;
import com.Testing.entities.Employee;
import org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest
{
    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp()
    {
        employee = Employee.builder()
                .email("jat2@gmail.com")
//                .id(1L)
                .name("jat")
                .salary(341L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmp()
    {
//      Arrange
        employeeRepository.save(employee);

//      Act, when
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());

        //assert then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.getFirst().getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmpList()
    {
        String email = "notPresent@gmail.com";
        List<Employee> employeeList = employeeRepository.findByEmail(email);

        //assert then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();
    }
}
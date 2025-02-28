package com.Testing.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private Long salary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(getId(), employee.getId()) && Objects.equals(getEmail(), employee.getEmail()) && Objects.equals(getName(), employee.getName()) && Objects.equals(getSalary(), employee.getSalary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getName(), getSalary());
    }
}

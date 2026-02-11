package com.example.management.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad OrderEntity.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
}

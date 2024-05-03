package org.example.dao;

import org.example.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentDAO extends JpaRepository<BinaryContent,Long> {
}

package org.example.dao;

import org.example.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDocumentDAO extends JpaRepository<AppDocument,Long> {
}

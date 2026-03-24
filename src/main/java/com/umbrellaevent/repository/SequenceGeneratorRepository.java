package com.umbrellaevent.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umbrellaevent.entity.SequenceGenerator;

public interface SequenceGeneratorRepository extends JpaRepository<SequenceGenerator, String> {
}

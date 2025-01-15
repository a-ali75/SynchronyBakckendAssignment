package com.assignment.synchrony.repository;

import com.assignment.synchrony.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

package com.example.blogproject.repository;

import com.example.blogproject.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>{
}

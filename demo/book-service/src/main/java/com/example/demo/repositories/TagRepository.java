package com.example.demo.repositories;

import com.example.demo.entity.Tag;
import com.example.demo.repositories.exceptions.BookNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> { }

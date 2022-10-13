package com.example.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface People extends JpaRepository<Person,String> {}

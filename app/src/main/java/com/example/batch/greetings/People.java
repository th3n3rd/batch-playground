package com.example.batch.greetings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface People extends JpaRepository<Person,String> {}

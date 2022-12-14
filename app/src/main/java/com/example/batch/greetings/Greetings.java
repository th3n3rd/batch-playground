package com.example.batch.greetings;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface Greetings extends JpaRepository<Greeting, UUID>{}

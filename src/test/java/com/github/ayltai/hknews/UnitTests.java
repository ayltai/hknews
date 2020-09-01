package com.github.ayltai.hknews;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {
    "common",
    "test",
})
public abstract class UnitTests {
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }
}

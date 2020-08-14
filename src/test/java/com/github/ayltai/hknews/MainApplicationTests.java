package com.github.ayltai.hknews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.ayltai.hknews.controller.ItemController;
import com.github.ayltai.hknews.controller.SourceController;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SpringBootTest
public final class MainApplicationTests extends UnitTests {
    @Autowired
    private SourceController sourceController;

    @Autowired
    private ItemController itemController;

    @Test
    public void testContextLoads() {
        Assertions.assertNotNull(this.sourceController);
        Assertions.assertNotNull(this.itemController);
    }
}

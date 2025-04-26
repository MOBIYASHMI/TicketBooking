package com.example.application.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {

    private final HomeController homeController = new HomeController();

    @Test
    public void testHome() {
        String view = homeController.home();
        assertEquals("frontpage", view);
    }

    @Test
    public void testDashboard() {
        String view = homeController.dashboard();
        assertEquals("dashboard", view);
    }

    @Test
    public void testAbout() {
        String view = homeController.about();
        assertEquals("about", view);
    }

    @Test
    public void testServices() {
        String view = homeController.services();
        assertEquals("service", view);
    }

    @Test
    public void testContact() {
        String view = homeController.contact();
        assertEquals("contact", view);
    }
}

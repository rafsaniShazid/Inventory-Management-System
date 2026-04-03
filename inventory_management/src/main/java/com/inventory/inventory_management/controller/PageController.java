package com.inventory.inventory_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC Page Controller for rendering Thymeleaf templates.
 * Separate from REST controllers to handle view rendering only.
 */
@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/dashboard";
    }

    @GetMapping("/request")
    public String request() {
        return "pages/request";
    }

    @GetMapping("/my-requests")
    public String myRequests() {
        return "pages/my-requests";
    }

    @GetMapping("/items")
    public String items() {
        return "pages/items";
    }

    @GetMapping("/categories")
    public String categories() {
        return "pages/categories";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}

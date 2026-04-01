package com.inventory.inventory_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC Page Controller for rendering Thymeleaf templates.
 * Separate from REST controllers to handle view rendering only.
 */
@Controller
public class PageController {

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

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}

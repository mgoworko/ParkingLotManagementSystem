package com.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HTMLProviderController {
    @RequestMapping("/")
    public String indexHTML() {
        return "index";
    }
}

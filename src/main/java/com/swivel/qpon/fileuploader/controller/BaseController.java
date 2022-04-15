package com.swivel.qpon.fileuploader.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base Controller
 */
@RestController
@CrossOrigin
public class BaseController {

    /**
     * index action
     *
     * @return html content
     */
    @GetMapping(path = "/", produces = "text/html")
    public String index() {
        return "<h1>File Service</h1>";
    }
}

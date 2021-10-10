package com.teammander.salamander.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class IndexController {
    @RequestMapping(method=RequestMethod.GET, value="/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html");
    }

}

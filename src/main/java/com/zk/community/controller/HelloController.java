package com.zk.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alpha")
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello, spring";
    }

    @RequestMapping(path="/student")
    public String getStudent(String name, String age, Model model) {

        model.addAttribute("name", name);
        model.addAttribute("age",age);
        return "/views";
    }

}

package com.example.libraryApplicationDemo.controller;

import com.example.libraryApplicationDemo.Service.UserService;
import com.example.libraryApplicationDemo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Controller
public class UserController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

    @GetMapping("/user/login")
    public String showLoginForm() {
        return "user/login";
    }

    @PostMapping("/user/login")
    public String processUserLogin(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userService.getUserByUsername(username);
        if (user!=null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            User loggedUser = userService.getUserByUsername(username);
            String url = "http://localhost:" +user.getPort() + "/api/nodes/login";
            ResponseEntity<String> response=  restTemplate.postForEntity(url, httpEntity, String.class);
            userService.setLoggedUser(loggedUser);

            model.addAttribute("username", username);

            System.out.println(response);
            if(Objects.requireNonNull(response.getBody()).contains("unauthenticated"))
                return "user/login";
            else
                return "user/menuPage";
        }
     return "user/login";
    }

    @GetMapping("/user/signup")
    public String showUserSignupPage() {
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String processUserSignup(@RequestParam String username, @RequestParam String password, Model model) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/api/bootstrap/registerUser", httpEntity, String.class);

        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String nodeName = root.get("assignedNode").asText();
        Integer port = root.get("port").asInt();

        User newUser = new User(username, password);
        newUser.setAssignedNode(nodeName);
        newUser.setPort(port);

        userService.addUser(newUser);
        System.out.println("user : " + username + " was assigned to node : " + nodeName);
        model.addAttribute("response", responseBody);

        return "redirect:/";
    }


}



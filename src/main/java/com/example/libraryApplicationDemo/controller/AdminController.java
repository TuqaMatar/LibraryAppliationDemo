package com.example.libraryApplicationDemo.controller;

import com.example.libraryApplicationDemo.Service.UserService;
import com.example.libraryApplicationDemo.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class AdminController {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

    @PostMapping("/admin/login")
    public String processAdminLogin(@RequestParam String username, @RequestParam String password, Model model) {
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
                return "adminLogin";
            else
                return "admin/adminPage";
        }
        return "admin/adminLogin";
    }

    @GetMapping("/admin/login")
    public String getAdminLogin(){
        return "admin/adminLogin";
    }

    @GetMapping("/admin/manageDatabase")
    public String getMangeDatabase(){
        return "admin/manageDatabase";
    }

    @GetMapping("/admin/addDatabase")
    public String addDatabase(){
        return "admin/addDatabase";
    }

    @PostMapping("/admin/addDatabase")
    public String addDatabase(HttpServletRequest request) throws JsonProcessingException {
        String name = request.getParameter("name");
        String schemaText = request.getParameter("schema");

        // Convert the schemaText to a JSON object
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaJson = null;
        try {
            schemaJson = mapper.readTree(schemaText);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Create a JSON object representing the database
        Map<String, Object> database = new HashMap<>();
        database.put("name", name);
        database.put("schema", schemaJson);

        // Send the HTTP request to create the database
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"name\":\"" + name+ "\",\"schema\":" + mapper.writeValueAsString(schemaJson) + "}";
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        String url = "http://localhost:" + "8081"+ "/api/database/createDatabase?broadcast=true";
        ResponseEntity<String> response= restTemplate.postForEntity(url, httpEntity, String.class);

        System.out.println(response);
        return "admin/addDatabase";
    }

}



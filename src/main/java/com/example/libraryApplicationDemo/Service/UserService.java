package com.example.libraryApplicationDemo.Service;

import com.example.libraryApplicationDemo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {
    User loggedUser;
    @Autowired
    RestTemplate restTemplate;

    private List<User> users;

    public UserService() {
    }

    @PostConstruct
    public void init() {
        users = new ArrayList<>();
        // Send the HTTP request to the server
        String url = "http://localhost:8081/api/nodes/getNodeUsers";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Extract the user data from the JSON response

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                Iterator<Map.Entry<String, JsonNode>> fieldsIterator = root.fields();

                while (fieldsIterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = fieldsIterator.next();
                    String username = field.getKey();

                    JsonNode userNode = field.getValue();
                    String id = userNode.get("id").asText();
                    String password = userNode.get("password").asText();
                    String assignedNode = userNode.get("assignedNode").asText();
                    int port = userNode.get("port").asInt();
                        System.out.println(username);
                        User user = new User(UUID.fromString(id), username, password, assignedNode, port);
                        users.add(user);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void addUser(User newUser) {
        UUID id = UUID.randomUUID();
        newUser.setId(id);

        this.users.add(newUser);
    }

    public User getUserByUsername(String username) {
        for (User user : this.users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        // Return null if the user is not found
        return null;
    }

    public boolean verifyPassword(String username, String password) {
        User user = getUserByUsername(username);

        if (user == null) {
            return false;
        }

        return user.getPassword().equals(password);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}

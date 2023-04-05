package com.example.libraryApplicationDemo.controller;

import com.example.libraryApplicationDemo.Service.UserService;
import com.example.libraryApplicationDemo.model.Book;
import com.example.libraryApplicationDemo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookController {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

    private String DATABASE_NAME="book" ;

    @GetMapping("/book/add")
    public String addBook() {
        return "book/addBook";
    }

    @PostMapping("/book/add")
    public String addBook(HttpServletRequest request) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        int year = Integer.parseInt(request.getParameter("year"));
        String genre = request.getParameter("genre");

        Book book = new Book(title, author, year, genre);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"title\":\"" + book.getTitle() + "\",\"author\":\"" + book.getAuthor() + "\",\"year\":" + book.getYear() + ",\"genre\":\"" + book.getGenre() + "\"}";

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        User loggedUser = userService.getLoggedUser();

        String url = "http://localhost:" +loggedUser.getPort() + "/api/document/"+DATABASE_NAME+"/createDocument";
        ResponseEntity<String> response=  restTemplate.postForEntity(url, httpEntity, String.class);

        System.out.println(response);
        return "user/menuPage";
    }


    @GetMapping("/book/view")
    public String viewBooks(Model model) {
        // Send the HTTP request to the server
        User loggedUser = userService.getLoggedUser();
        String url = "http://localhost:"+loggedUser.getPort()+"/api/database/book/getDocuments";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Extract the book data from the JSON response
        List<Map<String, Object>> books = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        int id = node.get("id").asInt();

                        JsonNode dataNode = node.get("data");
                        String title = dataNode.get("title").asText();
                        String author = dataNode.get("author").asText();
                        int year = dataNode.get("year").asInt();
                        String genre = dataNode.get("genre").asText();

                        Map<String, Object> book = new HashMap<>();
                        book.put("title", title);
                        book.put("author", author);
                        book.put("year", year);
                        book.put("genre", genre);
                        book.put("id" , id);
                        books.add(book);
                    }
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Pass the book data to the view
        model.addAttribute("books", books);
        return "book/viewBooks";
    }

    @PostMapping("book/ShowUpdate/{bookId}")
    public String showUpdateBook(@PathVariable("bookId") Long bookId , Model model) {
        // Send the HTTP request to the server to get the book data
        User loggedUser = userService.getLoggedUser();
        String url = "http://localhost:"+loggedUser.getPort()+"/api/database/book/"+bookId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Extract the book data from the JSON response
        Book book = null;
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                Integer id = root.get("id").asInt();
                JsonNode dataNode = root.get("data");
                String title = dataNode.get("title").asText();
                String author = dataNode.get("author").asText();
                int year = dataNode.get("year").asInt();
                String genre = dataNode.get("genre").asText();

                book = new Book(title, author, year, genre);
                book.setId(id);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Pass the book object to the viewv
        model.addAttribute("book", book);
        return "book/updateBook";

    }

    @PostMapping("book/update/{bookId}")
    public String updateBook(HttpServletRequest request , @PathVariable Integer bookId) {

        String title = request.getParameter("title");
        String author = request.getParameter("author");
        int year = Integer.parseInt(request.getParameter("year"));
        String genre = request.getParameter("genre");

        Book book = new Book(title, author, year, genre);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"title\":\"" + book.getTitle() + "\",\"author\":\"" + book.getAuthor() + "\",\"year\":" + book.getYear() + ",\"genre\":\"" + book.getGenre() + "\"}";

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        User loggedUser = userService.getLoggedUser();

        String url = "http://localhost:" +loggedUser.getPort() + "/api/document/"+DATABASE_NAME+"/updateDocument/"+ bookId;
        ResponseEntity<String> response=  restTemplate.postForEntity(url, httpEntity, String.class);


        System.out.println(response);
        return "redirect:/book/view";
    }

    @PostMapping("/book/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId, Model model) {
        User loggedUser = userService.getLoggedUser();

        String url = "http://localhost:"+loggedUser.getPort()+"/api/document/book/deleteDocument/"+bookId;
        restTemplate.postForObject(url, null ,Void.class);

        return "book/viewBooks";
    }


    @GetMapping("/book/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        User loggedUser = userService.getLoggedUser();
        String url = "http://localhost:" + loggedUser.getPort() + "/api/document/search/book/" + query;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Extract the book data from the JSON response
        List<Map<String, Object>> books = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        int id = node.get("id").asInt();

                        JsonNode dataNode = node.get("data");
                        String title = dataNode.get("title").asText();
                        String author = dataNode.get("author").asText();
                        int year = dataNode.get("year").asInt();
                        String genre = dataNode.get("genre").asText();

                        Map<String, Object> book = new HashMap<>();
                        book.put("title", title);
                        book.put("author", author);
                        book.put("year", year);
                        book.put("genre", genre);
                        book.put("id" , id);
                        books.add(book);
                    }
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Pass the book data to the view
        model.addAttribute("books", books);
        return "book/viewBooks";
    }
}

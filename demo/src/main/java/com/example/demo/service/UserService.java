package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // Initialize with some sample data
        users.add(new User(idGenerator.getAndIncrement(), "John Doe", "john.doe@example.com"));
        users.add(new User(idGenerator.getAndIncrement(), "Jane Smith", "jane.smith@example.com"));
    }

    public List<User> getAllUsers() {
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(users);
    }

    public User getUserById(Long id) {
        // Simulate some processing time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        
        return user.orElse(null);
    }

    public User createUser(User user) {
        // Simulate some processing time
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        user.setId(idGenerator.getAndIncrement());
        users.add(user);
        return user;
    }

    public User updateUser(Long id, User userDetails) {
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Optional<User> userOpt = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            return user;
        }
        
        return null;
    }

    public boolean deleteUser(Long id) {
        // Simulate some processing time
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return users.removeIf(u -> u.getId().equals(id));
    }

    public void processUsers() {
        // This method demonstrates logging of internal service methods
        System.out.println("Processing users...");
        for (User user : users) {
            System.out.println("Processing: " + user.getName());
        }
    }
}

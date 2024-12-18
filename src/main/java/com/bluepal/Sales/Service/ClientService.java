package com.bluepal.Sales.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bluepal.Sales.Entity.Client;
import com.bluepal.Sales.Entity.ClientComment;
import com.bluepal.Sales.Repository.ClientCommentRepo;
import com.bluepal.Sales.Repository.ClientRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ClientRepo clientRepo;
    
    @Autowired
    private ClientCommentRepo commentRepo;

    @Cacheable(value = "clients")
    public List<Client> getAllClients() {
        logger.info("Fetching all clients from the database.");
        List<Client> clients = clientRepo.findAll();
        logger.info("Fetched {} clients.", clients.size());
        return clients;
    }

    @Cacheable(value = "clients", key = "#id")
    public Optional<Client> getClientById(Long id) {
        logger.info("Fetching client with id: {}", id);
        Optional<Client> client = clientRepo.findById(id);
        if (client.isPresent()) {
            logger.info("Client found: {}", client.get().getFirstName());
        } else {
            logger.warn("Client with id {} not found.", id);
        }
        return client;
    }

    @CachePut(value = "clients", key = "#result.id")
    public Client createClient(Client client) {
        logger.info("Creating a new client with name: {}", client.getFirstName());
        Client savedClient = clientRepo.save(client);
        logger.info("Client created with id: {}", savedClient.getId());
        return savedClient;
    }

    @CachePut(value = "clients", key = "#id")
    public Optional<Client> updateClient(Long id, Client updatedClient) {
        logger.info("Updating client with id: {}", id);
        return clientRepo.findById(id).map(client -> {
            client.setFirstName(updatedClient.getFirstName());
            client.setMiddleName(updatedClient.getMiddleName());
            client.setLastName(updatedClient.getLastName());
            client.setAddress(updatedClient.getAddress());
            client.setEmail1(updatedClient.getEmail1());
            client.setEmail2(updatedClient.getEmail2());
            client.setMobileNo1(updatedClient.getMobileNo1());
            client.setMobileNo2(updatedClient.getMobileNo2());
            client.setRating(updatedClient.getRating());
            client.setStatus(updatedClient.getStatus());
            
            Client savedClient = clientRepo.save(client);
            logger.info("Client with id {} updated successfully.", id);
            return savedClient;
        });
    }

    @CacheEvict(value = "clients", key = "#id")
    public boolean deleteClient(Long id) {
        logger.info("Attempting to delete client with id: {}", id);
        if (clientRepo.existsById(id)) {
            clientRepo.deleteById(id);
            logger.info("Client with id {} deleted successfully.", id);
            return true;
        } else {
            logger.warn("Client with id {} does not exist.", id);
            return false;
        }
    }

    public ClientComment addComment(Long clientId, String commentText) {
        logger.info("Adding comment to client with id: {}", clientId);
        Optional<Client> clientOpt = clientRepo.findById(clientId);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            ClientComment comment = new ClientComment();
            comment.setClient(client);
            comment.setCommentText(commentText);
            comment.setCommentedAt(LocalDateTime.now());
            
            ClientComment savedComment = commentRepo.save(comment);
            logger.info("Comment added to client with id: {}", clientId);
            return savedComment;
        } else {
            logger.error("Client not found with id: {}", clientId);
            throw new RuntimeException("Client not found with id: " + clientId);
        }
    }

    public List<ClientComment> getCommentsByClientId(Long clientId) {
        logger.info("Fetching comments for client with id: {}", clientId);
        List<ClientComment> comments = commentRepo.findByClientId(clientId);
        logger.info("Fetched {} comments for client with id: {}", comments.size(), clientId);
        return comments;
    }
}

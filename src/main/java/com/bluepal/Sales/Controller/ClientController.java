package com.bluepal.Sales.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bluepal.Sales.Entity.Client;
import com.bluepal.Sales.Entity.ClientComment;
import com.bluepal.Sales.Service.ClientImportExportService;
import com.bluepal.Sales.Service.ClientService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientImportExportService importExportService;
    

    @GetMapping
    public List<Client> getAllClients(@RequestHeader("Authorization") String jwt) {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestHeader("Authorization") String jwt,@RequestBody Client client) {
        Client newClient = clientService.createClient(client);
        return ResponseEntity.ok(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody Client client) {
        Optional<Client> updatedClient = clientService.updateClient(id, client);
        return updatedClient.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        if (clientService.deleteClient(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{clientId}/comment")
    public ResponseEntity<ClientComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long clientId, @RequestBody String commentText) {
        ClientComment comment = clientService.addComment(clientId, commentText);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{clientId}/comment")
    public List<ClientComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long clientId) {
        return clientService.getCommentsByClientId(clientId);
    }
    
    
    @PostMapping("/download")
    public ResponseEntity<String> importClients(@RequestHeader("Authorization") String jwt,@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importClients(file);
            return ResponseEntity.ok("Clients imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import clients: " + e.getMessage());
        }
    }

    @GetMapping("/upload")
    public ResponseEntity<String> exportClients(@RequestHeader("Authorization") String jwt) {
        String xlsx = importExportService.exportClients();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=clients.xml")
                .body(xlsx);
    }
}

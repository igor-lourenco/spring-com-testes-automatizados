package com.expert.testes.controllers;

import com.expert.testes.DTOs.UserDTO;
import com.expert.testes.DTOs.UserWithPasswordDTO;
import com.expert.testes.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/users")
public class UserController {

    private final UserService service;

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> findAll() {
        log.info("REQUEST - GET [findAll]");

        List<UserDTO> userDTOs = service.findAll();

        log.info("RESPONSE - GET [findAll]");
        return userDTOs;
    }


    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/page")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDTO> findAllPaged(
        @PageableDefault(page = 0, size = 12, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("REQUEST - GET [findAllPaged]");

        Page<UserDTO> userDTOs = service.findAllPaged(pageable);

        log.info("RESPONSE - GET [findAllPaged]");
        return userDTOs;
    }


    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO findById(@PathVariable Long id) {
        log.info("REQUEST - GET [findById]");

        UserDTO userDTO = service.findById(id);

        log.info("RESPONSE - GET [findById]");
        return userDTO;
    }


    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserWithPasswordDTO userWithPasswordDTO) {
        log.info("REQUEST - POST [insert]");

        UserDTO userDTO = service.insert(userWithPasswordDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(userDTO.id())
            .toUri();

        log.info("RESPONSE - POST [insert]");
        return ResponseEntity.created(uri).body(userDTO);
    }


    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable Long id, @Valid  @RequestBody UserDTO dto) {
        log.info("REQUEST - PUT [update]");

        UserDTO userDTO = service.update(id, dto);

        log.info("RESPONSE - PUT [update]");
        return userDTO;
    }


    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("REQUEST - DELETE [delete]");

        service.delete(id);

        log.info("RESPONSE - DELETE [delete]");
    }
}

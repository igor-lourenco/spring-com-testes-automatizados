package com.expert.testes.services;

import com.expert.testes.DTOs.RoleDTO;
import com.expert.testes.DTOs.UserDTO;
import com.expert.testes.DTOs.UserWithPasswordDTO;
import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RoleRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.security.services.AuthSecurity;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSecurity authSecurity;


    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return repository.findAll().stream()
            .map(UserDTO::new)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<UserDTO> page = repository.findAll(pageable).map(UserDTO::new);

//        return new PageImpl<>(
//            new ArrayList<>(page.getContent()),
//            page.getPageable(),
//            page.getTotalElements());

        return page;
    }


    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = findUserById(id);

        User userAuthenticated = authSecurity.getUserId();
        log.info("User autenticado: {}", userAuthenticated.getEmail());

        return new UserDTO(user);
    }


    @Transactional
    public UserDTO insert(UserWithPasswordDTO userWithPasswordDTO) {
        try {

            User user = new User();
            convertUserWithPasswordDTOToUser(userWithPasswordDTO, user);

            user.getRoles().clear();
            user.getRoles().add(roleRepository.findByAuthority("ROLE_OPERATOR").get());

            user = repository.save(user);
            return new UserDTO(user);

        } catch (EntityNotFoundException e) {
            if (e.getCause() instanceof ObjectNotFoundException obj) {
                throw new EntidadeNotFoundException("Role não encontrado: " + obj.getIdentifier() + ", para associar com User");
            }
            throw e;
        }
    }


    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        try {

            User user = findUserById(id);
            convertUserDTOToUser(userDTO, user);
            return new UserDTO(user);

        } catch (EntityNotFoundException e) {
            if (e.getCause() instanceof ObjectNotFoundException obj) {
                throw new EntidadeNotFoundException("Role não encontrado: " + obj.getIdentifier() + ", para associar com User");
            }
            throw e;
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS) //Participa de uma transação existente, mas NÃO cria uma nova
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNotFoundException("User não encontrado: " + id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }


    private User findUserById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new EntidadeNotFoundException("User não encontrado: " + id));
    }


    private void convertUserDTOToUser(UserDTO userDTO, User user) {
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.getRoles().clear();

        if(userDTO.rolesDTO() == null) return;

        for (RoleDTO roleDTO : userDTO.rolesDTO()) {
            //getReferenceById ⇾ ideal para salvar ou atualizar relacionamentos sem precisar carregar dados desnecessários do banco.
            Role role = roleRepository.getReferenceById(roleDTO.id());
            user.getRoles().add(role);
        }
    }

    private void convertUserWithPasswordDTOToUser(UserWithPasswordDTO userWithPasswordDTO, User user) {
        user.setFirstName(userWithPasswordDTO.firstName());
        user.setLastName(userWithPasswordDTO.lastName());
        user.setEmail(userWithPasswordDTO.email());

        user.setPassword(passwordEncoder.encode(userWithPasswordDTO.password()));

        user.getRoles().clear();
//
//        if(userWithPasswordDTO.rolesDTO() == null) return;
//
//        for (RoleDTO roleDTO : userWithPasswordDTO.rolesDTO()) {
//            //getReferenceById ⇾ ideal para salvar ou atualizar relacionamentos sem precisar carregar dados desnecessários do banco.
//            Role role = roleRepository.getReferenceById(roleDTO.id());
//            user.getRoles().add(role);
//        }
    }
}

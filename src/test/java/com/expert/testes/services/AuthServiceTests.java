package com.expert.testes.services;


import com.expert.testes.DTOs.EmailDTO;
import com.expert.testes.entities.RecoverPassword;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RecoverPasswordRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.utils.EmailFactory;
import com.expert.testes.utils.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository repository;

    @Mock
    private RecoverPasswordRepository recoverPasswordRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    private User userExisting;
    private EmailDTO emailDTOExisting;
    @BeforeEach
    void setUp() {

        userExisting = UserFactory.createUserExisting();
        emailDTOExisting = EmailFactory.createEmailDTOExisting();

        ReflectionTestUtils.setField(authService, "expiration", "30");
        ReflectionTestUtils.setField(authService, "uriRecoverPassword", "http://localhost:3000/recover-password");
    }

//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test //  <recoverToken> deve <GerarTokenDeRecuperacao> [quando <EmailExistir>]
    void recoverTokenShouldGenerateRecoveryTokenWhenEmailExists() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findByEmail(emailDTOExisting.email()))
            .thenReturn(Optional.of(userExisting)); // repository.findByEmail → deve retornar User quando email existir

//      recoverPasswordRepository.save -> deve retornar o mesmo objeto do tipo RecoverPassword quando receber qualquer objeto do tipo RecoverPassword
        Mockito.when(recoverPasswordRepository.save(Mockito.any(RecoverPassword.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

//      ArgumentCaptor → serve para capturar o argumento que foi passado para um método mockado (ainda não tem nada dentro, apenas criando a variável...)
        ArgumentCaptor<RecoverPassword> recoverPasswordCaptor = ArgumentCaptor.forClass(RecoverPassword.class);
        ArgumentCaptor<String> emailToCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Instant before = Instant.now();


//      -> Act: execute as ações necessárias
        authService.recoverToken(emailDTOExisting);

        Instant after = Instant.now();


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'recoverPasswordRepository.save' que está dentro do 'authService.recoverToken' foi usado exatamente 1 vez
            recoverPasswordRepository, Mockito.times(1))
            .save(
                recoverPasswordCaptor.capture() // captura o objeto do tipo RecoverPassword que foi passado para o método save.
            );

        RecoverPassword recoverPassword = recoverPasswordCaptor.getValue(); // representa exatamente o RecoverPassword que o recoverPasswordRepository tentou salvar

        Assertions.assertEquals(emailDTOExisting.email(), recoverPassword.getEmail());
        Assertions.assertNotNull(recoverPassword.getToken());
        Assertions.assertFalse(recoverPassword.getToken().isBlank());

//      Como o tempo continua passando enquanto o teste executa. Se comparar a igualdade exata o teste iria falhar
        Instant expectedMinExpiration = before.plus(30, ChronoUnit.MINUTES); // A expiração mínima aceitável deve ser o momento antes da execução mais 30 minutos.
        Instant expectedMaxExpiration = after.plus(30, ChronoUnit.MINUTES); // A expiração máxima aceitável deve ser o momento depois da execução mais 30 minutos.

        Assertions.assertFalse(recoverPassword.getExpiration().isBefore(expectedMinExpiration)); // A expiração não pode ser antes do mínimo esperado.
        Assertions.assertFalse(recoverPassword.getExpiration().isAfter(expectedMaxExpiration)); // A expiração não pode ser depois do máximo esperado.


        Mockito.verify( // garante que o método do 'emailService.sendEmail' que está dentro do 'authService.recoverToken' foi usado exatamente 1 vez
            emailService, Mockito.times(1))
            .sendEmail(
                emailToCaptor.capture(),// captura a String email que foi passado para o método sendEmail
                subjectCaptor.capture(),    // captura a String subject que foi passado para o método sendEmail
                bodyCaptor.capture()        // captura a String body que foi passado para o método sendEmail
            );

        Assertions.assertEquals(emailDTOExisting.email(), emailToCaptor.getValue());
        Assertions.assertEquals("Recuperação de senha", subjectCaptor.getValue());

        String body = bodyCaptor.getValue(); // representa exatamente a String body que o emailService tentou enviar

        Assertions.assertTrue(body.contains("Acesse o link para definir uma nova senha"));
        Assertions.assertTrue(body.contains("http://localhost:3000/recover-password/" + recoverPassword.getToken()));
        Assertions.assertTrue(body.contains("Validade de 30 minutos."));


        Mockito.verify(// garante que o método do 'repository.findByEmail' que está dentro do 'authService.recoverToken' foi usado exatamente 1 vez
            repository, Mockito.times(1))
            .findByEmail(emailDTOExisting.email());
    }
}
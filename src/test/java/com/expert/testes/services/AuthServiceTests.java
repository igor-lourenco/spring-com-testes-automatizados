package com.expert.testes.services;


import com.expert.testes.DTOs.EmailDTO;
import com.expert.testes.DTOs.NewPasswordDTO;
import com.expert.testes.entities.RecoverPassword;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RecoverPasswordRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EmailException;
import com.expert.testes.utils.EmailFactory;
import com.expert.testes.utils.NewPasswordDTOFactory;
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
import java.util.List;
import java.util.Optional;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
class AuthServiceTests {

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private UserRepository repository;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private RecoverPasswordRepository recoverPasswordRepository;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private EmailService emailService;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private AuthService authService;


    private User userExisting;
    private EmailDTO emailDTOExisting;
    private EmailDTO emailDTONotExisting;
    private NewPasswordDTO newPasswordDTOWithTokenValid;
    private RecoverPassword recoverPassword;

    @BeforeEach
    void setUp() {
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação

        userExisting = UserFactory.createUserExisting();
        emailDTOExisting = EmailFactory.createEmailDTOExisting();
        emailDTONotExisting = EmailFactory.createEmailDTONotExisting();

        newPasswordDTOWithTokenValid = NewPasswordDTOFactory.createNewPasswordDTOWithTokenValid();

        recoverPassword = RecoverPassword.builder()
            .email(emailDTOExisting.email())
            .token(newPasswordDTOWithTokenValid.token())
            .expiration(Instant.now().plus(30, ChronoUnit.MINUTES))
            .build();


        ReflectionTestUtils.setField(authService, "expiration", "30");
        ReflectionTestUtils.setField(authService, "uriRecoverPassword", "http://localhost:3000/recover-password");
    }


//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test //  <recoverToken> deve <GerarTokenDeRecuperacao> [quando <EmailExistir>]
    public void recoverTokenShouldGenerateRecoveryTokenWhenEmailExists() {
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


    @Test //  <recoverToken> deve <LancarEmailException> [quando <EmailNaoExistir>]
    public void recoverTokenShouldThrowEmailExceptionWhenEmailDoesNotExists() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findByEmail(emailDTONotExisting.email()))
            .thenReturn(Optional.empty()); // repository.findByEmail → deve retornar Optional vazio quando email não existir


//      -> Act: execute as ações necessárias
        EmailException ex = Assertions.assertThrows(EmailException.class, () -> {
            authService.recoverToken(emailDTONotExisting);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Email não encontrado", ex.getMessage());


        Mockito.verify( // garante que o método 'repository.findByEmail' que está dentro do 'authService.recoverToken' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findByEmail(emailDTONotExisting.email());
    }


    @Test //  <saveNewPassword> deve <SalvarNovaSenha> [quando <EmailExistirETokenEhValido>]
    public void saveNewPasswordShouldSaveNewPasswordWhenEmailExistsAndTokenIsValid() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(recoverPasswordRepository.searchValidTokens(Mockito.eq(newPasswordDTOWithTokenValid.token()),Mockito.any(Instant.class)))
            .thenReturn(List.of(recoverPassword)); // recoverPasswordRepository.searchValidTokens → deve retornar List<RecoverPassword> quando token e o Instant forem válidos

        Mockito.when(repository.findByEmail(emailDTOExisting.email()))
            .thenReturn(Optional.of(userExisting));  // repository.findByEmail → deve retornar User quando email existir

        String encodedPassword = "senha-criptografada";
        Mockito.when(passwordEncoder.encode(newPasswordDTOWithTokenValid.password()))
            .thenReturn(encodedPassword); // passwordEncoder.encode → deve retornar a senha criptografada

//      repository.save -> deve retornar o mesmo objeto do tipo User quando receber qualquer objeto do tipo User
        Mockito.when(repository.save(Mockito.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));


//      -> Act: execute as ações necessárias
        authService.saveNewPassword(newPasswordDTOWithTokenValid);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        ArgumentCaptor<User> userCaptor =  // serve para capturar o argumento que foi passado para um método mockado (ainda não tem nada dentro)
            ArgumentCaptor.forClass(User.class);


        Mockito.verify( //  garante que o método do 'repository.save' que está dentro do 'authService.saveNewPassword' foi usado exatamente 1 vez
            repository, Mockito.times(1))
            .save(
                userCaptor.capture() // // captura o objeto User que foi passado para o método save;
            );

        User savedUser = userCaptor.getValue(); // representa exatamente o User que o repository tentou salvar

        Assertions.assertEquals(emailDTOExisting.email(), savedUser.getEmail());
        Assertions.assertEquals(encodedPassword, savedUser.getPassword());


//      garante que o método do 'recoverPasswordRepository.searchValidTokens' que está dentro do 'authService.saveNewPassword' foi usado exatamente 1 vez
        Mockito.verify(recoverPasswordRepository, Mockito.times(1))
            .searchValidTokens(Mockito.eq(newPasswordDTOWithTokenValid.token()), Mockito.any(Instant.class));


//      garante que o método do 'repository.findByEmail' que está dentro do 'authService.saveNewPassword' foi usado exatamente 1 vez
        Mockito.verify(repository, Mockito.times(1))
            .findByEmail(emailDTOExisting.email());


//      garante que o método do 'passwordEncoder.encode' que está dentro do 'authService.saveNewPassword' foi usado exatamente 1 vez
        Mockito.verify(passwordEncoder, Mockito.times(1))
            .encode(newPasswordDTOWithTokenValid.password());

    }
}
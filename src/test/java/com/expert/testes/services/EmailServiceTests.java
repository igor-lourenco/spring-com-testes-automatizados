package com.expert.testes.services;

import com.expert.testes.services.exceptions.EmailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class EmailServiceTests {

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private EmailService emailService;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private JavaMailSender emailSender;

    private String to, subject, body;

    @BeforeEach
    void setUp() {
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação

        to = "destinatario@teste.com";
        subject = "Assunto de teste";
        body = "Corpo do e-mail";

//      Como o teste unitário não sobe o contexto Spring inteiro, o campo 'emailFrom' não é preenchido pelo @Value
        ReflectionTestUtils.setField(
            emailService,
            "emailFrom",
            "noreply@teste.com"
        );
    }


    @Test //  <sendEmail> deve <EnviarEmailComSucesso> [quando <>]
    public void sendEmailShouldSendEmailSuccessfully() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ArgumentCaptor<SimpleMailMessage> captor =  // serve para capturar o argumento que foi passado para um método mockado (ainda não tem nada dentro)
            ArgumentCaptor.forClass(SimpleMailMessage.class);


//      -> Act: execute as ações necessárias
        emailService.sendEmail(to, subject, body);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify(emailSender) //  garante que o método do 'emailSender.send' que está dentro do 'emailService.sendEmail' foi chamado
            .send(captor.capture()); // captura o objeto SimpleMailMessage que foi passado para o método send;

        SimpleMailMessage message = captor.getValue(); // representa exatamente o SimpleMailMessage que o service tentou enviar.

        Assertions.assertEquals("noreply@teste.com", message.getFrom());
        Assertions.assertEquals(to, message.getTo()[0]);
        Assertions.assertEquals(subject, message.getSubject());
        Assertions.assertEquals(body, message.getText());
    }


    @Test //  <sendEmail> deve <LancarEmailException> [quando <FalharAoEnviarEmail>]
    public void sendEmailShouldThrowEmailExceptionWhenFailingToSendEmail() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doThrow(new MailSendException("Erro ao enviar e-mail"))  //  emailSender.send -> lança exceção quando falhar no envio de email
            .when(emailSender)
            .send(Mockito.any(SimpleMailMessage.class));


//      -> Act: execute as ações necessárias
        EmailException ex = Assertions.assertThrows(EmailException.class, () ->
            emailService.sendEmail(to, subject, body)
        );


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Failed to send email: Erro ao enviar e-mail", ex.getMessage());


        Mockito.verify( // garante que o método do 'emailSender.send' que está dentro do 'emailService.sendEmail' foi usado exatamente 1 vez
                emailSender, Mockito.times(1))
            .send(Mockito.any(SimpleMailMessage.class));
    }
}

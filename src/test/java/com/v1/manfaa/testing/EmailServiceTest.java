package com.v1.manfaa.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private String fromEmail;

    @BeforeEach
    void setUp() {
        fromEmail = "noreply@test.com";
        ReflectionTestUtils.setField(emailService, "email", fromEmail);
    }

    @Test
    void sendEmail_ShouldSendEmailWithCorrectDetails() {
        // Arrange
        String toEmail = "recipient@test.com";
        String subject = "Test Subject";
        String body = "Test email body";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertNotNull(capturedMessage);
        assertEquals(fromEmail, capturedMessage.getFrom());
        assertArrayEquals(new String[]{toEmail}, capturedMessage.getTo());
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(body, capturedMessage.getText());
    }

    @Test
    void sendEmail_ShouldHandleMultilineBody() {
        // Arrange
        String toEmail = "user@test.com";
        String subject = "Multi-line Test";
        String body = "Line 1\nLine 2\nLine 3";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals(body, capturedMessage.getText());
        assertTrue(capturedMessage.getText().contains("\n"));
    }

    @Test
    void sendEmail_ShouldHandleSpecialCharactersInSubject() {
        // Arrange
        String toEmail = "test@example.com";
        String subject = "Test & Special <Characters> 123!";
        String body = "Test body";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals(subject, capturedMessage.getSubject());
    }

    @Test
    void sendEmail_ShouldCallMailSenderOnce() {
        // Arrange
        String toEmail = "recipient@test.com";
        String subject = "Test";
        String body = "Body";

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_ShouldHandleEmptyBody() {
        // Arrange
        String toEmail = "test@test.com";
        String subject = "Empty Body Test";
        String body = "";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals("", capturedMessage.getText());
    }

    @Test
    void sendEmail_ShouldHandleLongBody() {
        // Arrange
        String toEmail = "test@test.com";
        String subject = "Long Body Test";
        String body = "Lorem ipsum dolor sit amet, ".repeat(100);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals(body, capturedMessage.getText());
        assertTrue(capturedMessage.getText().length() > 1000);
    }
}

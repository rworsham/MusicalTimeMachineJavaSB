package com.musicaltimemachine.backend.service;

import com.mailjet.client.ClientOptions;
import com.musicaltimemachine.backend.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ContactService {

    @Value("${MJ_APIKEY_PUBLIC}")
    private String mailjetApiKey;

    @Value("${MJ_APIKEY_PRIVATE}")
    private String mailjetApiSecret;

    @Value("${MAILJET_SENDER_EMAIL}")
    private String senderEmail;

    @Value("${MAILJET_SENDER_NAME}")
    private String senderName;

    @Value("${MAILJET_RECIPIENT_EMAIL}")
    private String recipientEmail;

    public String sendEmail(ContactRequest contactRequest) {
        try {
            MailjetClient client = new MailjetClient(ClientOptions.builder()
                    .apiKey(mailjetApiKey)
                    .apiSecretKey(mailjetApiSecret)
                    .build());

            MailjetRequest request = new MailjetRequest(com.mailjet.client.resource.Emailv31.resource)
                    .property(com.mailjet.client.resource.Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put("From", new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", senderName))
                                    .put("To", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", recipientEmail)
                                                    .put("Name", "Support")))
                                    .put("Subject", "New Contact Form Submission")
                                    .put("TextPart", "Email: " + contactRequest.getEmail() + "\n\n" + contactRequest.getMessage())
                                    .put("HTMLPart", "<h3>Contact Form Message</h3>" +
                                            "<p><strong>From:</strong> " + contactRequest.getEmail() + "</p>" +
                                            "<p><strong>Message:</strong><br/>" + contactRequest.getMessage() + "</p>")
                            ));

            MailjetResponse response = client.post(request);

            if (response.getStatus() == 200) {
                return "Email sent successfully.";
            } else {
                throw new MailjetException("Failed to send email: " + response.getData().toString());
            }

        } catch (MailjetException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }
}

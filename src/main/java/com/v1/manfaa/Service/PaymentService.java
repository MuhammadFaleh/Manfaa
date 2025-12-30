package com.v1.manfaa.Service;
import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.PaymentDTOIn;
import com.v1.manfaa.DTO.Out.PaymentDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Payment;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @Value("${moyasar.api.key}")
    private String apiKey;
    @Value("${app.base.url}")
    private String baseUrl;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<PaymentDTOOut> processPayment(PaymentDTOIn dto, Integer userId){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);

        if(companyProfile == null){
            throw new ApiException("no company found");
        }

        String callBack = baseUrl + "/api/v1/payments/callback";
        String requestBody = String.format(
                "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s&"+
                        "source[month]=%s&source[year]=%s&amount=%d&currency=%s&callback_url=%s",
                dto.getName(),
                dto.getNumber(),
                dto.getCvc(),
                dto.getMonth(),
                dto.getYear(),
                (int) (dto.getAmount() * 100),
                dto.getCurrency(),
                callBack
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentDTOOut> response = restTemplate.exchange(MOYASAR_API_URL, HttpMethod.POST, entity, PaymentDTOOut.class);

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new ApiException("payment failed");
        }

        Payment payment = new Payment(null,response.getBody().getId(),response.getBody().getStatus(),response.getBody().getAmount() / 100,
                response.getBody().getCurrency(),response.getBody().getDescription(), LocalDateTime.now(),companyProfile);
        paymentRepository.save(payment);
        response.getBody().setAmount(response.getBody().getAmount() / 100);
        return response;
    }

    public ResponseEntity<PaymentDTOOut>  getPayment(String paymentId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PaymentDTOOut> response = restTemplate.exchange(MOYASAR_API_URL + paymentId, HttpMethod.GET, entity, PaymentDTOOut.class);

        if(!response.getBody().getStatus().equalsIgnoreCase("paid")){
            throw new ApiException("payment failed");
        }

        Payment payment = paymentRepository.findByPaymentId(paymentId);
        payment.setStatus(response.getBody().getStatus());

        paymentRepository.save(payment);
        response.getBody().setAmount(response.getBody().getAmount() / 100);
        return response;
    }
}
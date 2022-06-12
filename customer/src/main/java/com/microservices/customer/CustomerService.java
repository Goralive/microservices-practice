package com.microservices.customer;

import com.microservices.amqp.RabbitMQMessageProducer;
import com.microservices.clients.fraud.FraudCheckResponse;
import com.microservices.clients.fraud.FraudClient;
import com.microservices.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer messageProducer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraud");
        }
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Welcome %s %s", customer.getFirstName(), customer.getLastName())
        );
        messageProducer.publish(notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key");
    }
}

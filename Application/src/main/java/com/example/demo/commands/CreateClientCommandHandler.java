package com.example.demo.commands;

import com.example.demo.ClientValidator;
import com.example.demo.core.Client;
import com.example.demo.infrasrtucture.CreateRepository;
import com.example.demo.infrasrtucture.MockClientRepository;
import com.example.demo.messages.CreateClientRequest;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CreateClientCommandHandler {

    CreateRepository<Client> clients = new MockClientRepository();

    public static Function<CreateClientRequest, Client> projectToClient =
            createClientRequest -> new Client(
                    createClientRequest.getName(),
                    createClientRequest.getEmployeeId()
            );


    @RabbitListener(queues = "CommandClientQueue")
    public Either<Seq<String>, Integer> listen(CreateClientRequest createClientRequest) {

        return ClientValidator
                .validate(createClientRequest)
                .toEither()
                .map(projectToClient)
                .flatMap(clients::add);

    }
}

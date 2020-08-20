package com.example.demo.queries;

import com.example.demo.core.Client;
import com.example.demo.core.Employee;
import com.example.demo.infrasrtucture.MockClientRepository;
import com.example.demo.infrasrtucture.MockEmployeeRepository;
import com.example.demo.infrasrtucture.Repository;
import io.vavr.control.Either;
import com.example.demo.operators.EitherExtensions;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.example.demo.operators.EitherExtensions.mapT;
import static com.example.demo.operators.EitherExtensions.throwableMessage;
import static io.vavr.API.Try;

@Component
public class GetClientCommandHandler {

    Repository<Client> clients = new MockClientRepository();
    Repository<Employee> employees = new MockEmployeeRepository();

    @RabbitListener(queues = "QueryClientQueue")
    public Either<String, String> listen(Integer clientId) {

   var searched = clients
                .getById(clientId)
                .thenApplyAsync(mapT(Client::getEmployeeId))
                .thenComposeAsync(EitherExtensions.bindT(employees::getById));

        return Try(searched::get)
                .map(mapT(Employee::getName))
                .getOrElseGet(throwableMessage());

    }
}

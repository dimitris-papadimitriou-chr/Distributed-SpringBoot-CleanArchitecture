package com.example.demo;

import com.example.demo.core.Employee;
import com.example.demo.infrasrtucture.MockEmployeeRepository;
import com.example.demo.infrasrtucture.Repository;
import com.example.demo.messages.CreateClientRequest;
import io.vavr.API;
import io.vavr.collection.CharSeq;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import static com.example.demo.operators.EitherExtensions.mapT;
import static com.example.demo.operators.EitherExtensions.throwableMessage;

public class ClientValidator {
    public static Validation<Seq<String>, CreateClientRequest> validate(CreateClientRequest request) {
        return Validation.combine(validateName(request.getName()),
                validateEmployeeExists(request.getEmployeeId()))
                .ap(CreateClientRequest::new);
    }

    private static final String VALID_NAME_CHARS = "[a-zA-Z ]";

    public static Validation<String, String> validateName(String name) {
        return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(seq -> seq.isEmpty()
                ? Validation.valid(name)
                : Validation.invalid("Name contains invalid characters: '"
                + seq.distinct().sorted() + "'"));
    }

    public static Validation<String, Integer> validateEmployeeExists(int id) {
        Repository<Employee> employees = new MockEmployeeRepository();

        return API.Try(employees.getById(id)::get)
                .map(mapT(Employee::getId))
                .getOrElseGet(throwableMessage())
                .toValidation();

    }

}
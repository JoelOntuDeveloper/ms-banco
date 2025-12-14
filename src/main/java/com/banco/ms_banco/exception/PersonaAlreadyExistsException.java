package com.banco.ms_banco.exception;

public class PersonaAlreadyExistsException extends RuntimeException {
    public PersonaAlreadyExistsException(String message) {
        super(message);
    }
    
    public PersonaAlreadyExistsException(String campo, String valor) {
        super("Ya existe una persona con " + campo + ": " + valor);
    }
}
package com.banco.ms_banco.exception;

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(String message) {
        super(message);
    }
    
    public CuentaNotFoundException(Long cuentaId) {
        super("Cuenta no encontrada con ID: " + cuentaId);
    }
    
    public CuentaNotFoundException(String numeroCuenta, boolean porNumero) {
        super("Cuenta no encontrada con número: " + numeroCuenta);
    }
}

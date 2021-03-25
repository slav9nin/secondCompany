package com.secretcompany.exception;

public class TeamCountUnexpectedValueException extends TournamentUnexpectedValueException {
    public TeamCountUnexpectedValueException() {
        super("Should be two teams");
    }
}

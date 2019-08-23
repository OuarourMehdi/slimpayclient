package com.exemple.model;

import java.time.LocalDateTime;

public class SlimpayMandate {

    private String reference;
    private LocalDateTime dateSigned;
    private Status status;
    private String bankName;
    private String bic;
    private String iban;

    public enum Status {
        CREATED("created", "Crée"),
        WAITING_FOR_REFERENCE("waitingForReference", "En attente de référence"),
        WAITING_FOR_VALIDATION("waitingForValidation", "En attente de validation"),
        ACTIVE("active", "Actif"),
        REJECTED("rejected", "Rejeté"),
        REVOKED("revoked", "Révoqué"),
        EXPIRED("expired", "Expiré"),
        UNKNOWN("unknown", "Non connu");

        String code;
        String text;

        Status(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Status fromCode(String code) {
            for (Status status : Status.values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return status;
                }
            }
            return UNKNOWN;
        }
    }

    public String getReference() {
        return reference;
    }

    public SlimpayMandate setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public LocalDateTime getDateSigned() {
        return dateSigned;
    }

    public SlimpayMandate setDateSigned(LocalDateTime dateSigned) {
        this.dateSigned = dateSigned;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public SlimpayMandate setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getBankName() {
        return bankName;
    }

    public SlimpayMandate setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public String getBic() {
        return bic;
    }

    public SlimpayMandate setBic(String bic) {
        this.bic = bic;
        return this;
    }

    public String getIban() {
        return iban;
    }

    public SlimpayMandate setIban(String iban) {
        this.iban = iban;
        return this;
    }
}

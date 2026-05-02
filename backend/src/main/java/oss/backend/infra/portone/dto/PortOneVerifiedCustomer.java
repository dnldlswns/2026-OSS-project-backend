package oss.backend.infra.portone.dto;

public record PortOneVerifiedCustomer(
                String ci,
                String di,
                String name,
                String gender,
                String birthDate,
                String phoneNumber,
                Boolean isForeigner) {
}

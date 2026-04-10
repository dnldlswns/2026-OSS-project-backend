package oss.backend.infra.portone.dto;

public record PortOneVerificationResponse(
                String status,
                PortOneVerifiedCustomer verifiedCustomer) {

}

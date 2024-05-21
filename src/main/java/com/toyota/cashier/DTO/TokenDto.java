package com.toyota.cashier.DTO;

public class TokenDto {
    private Integer id;

    private String token;



    private RolesDto rolesDto;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public RolesDto getAdminDto() {
        return rolesDto;
    }

    public void setAdminDto(RolesDto rolesDto) {
        this.rolesDto = rolesDto;
    }
}

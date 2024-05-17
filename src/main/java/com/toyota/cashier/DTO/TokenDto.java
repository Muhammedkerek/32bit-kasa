package com.toyota.cashier.DTO;

public class TokenDto {
    private Integer id;

    private String token;



    private AdminDto adminDto;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public AdminDto getAdminDto() {
        return adminDto;
    }

    public void setAdminDto(AdminDto adminDto) {
        this.adminDto = adminDto;
    }
}

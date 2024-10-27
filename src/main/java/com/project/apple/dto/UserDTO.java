
package com.project.apple.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String phoneNumber;
    private String password;
    private Long roleId;
}

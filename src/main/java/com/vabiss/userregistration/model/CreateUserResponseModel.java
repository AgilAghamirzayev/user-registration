package com.vabiss.userregistration.model;

import com.vabiss.userregistration.entity.enums.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateUserResponseModel {
    private Long id;
    private String name;
    private String email;
    private Status status;
}

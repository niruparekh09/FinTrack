package com.fintrack.api.auth;

import com.fintrack.api.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mapper class for Auth Service
@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "createdAt", ignore = true)
    User toUser(RegisterRequest request, String encodedPassword);

}

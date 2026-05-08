package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.UserOutput;
import com.example.finly.finance.domain.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserOutput toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    User toEntity(UserInput input);

    List<UserOutput> toDtoList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    void updateEntity(UserInput input, @MappingTarget User user);
}

package com.extrade.usermanagement.service;

import com.extrade.usermanagement.dto.UserAccountDto;


public interface UserManagmentService {

    long countUsersByEmailAddress(String emailAddress);

    long countUserByMobileNo(String mobileNo);

    long registerCustomer(UserAccountDto userAccountDto);


   ;
}

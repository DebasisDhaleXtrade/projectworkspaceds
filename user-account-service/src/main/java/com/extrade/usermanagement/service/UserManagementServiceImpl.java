package com.extrade.usermanagement.service;

import com.extrade.connect.beans.notification.MailNotification;
import com.extrade.connect.manager.NotificationManager;
import com.extrade.usermanagement.dto.UserAccountDto;
import com.extrade.usermanagement.entities.Role;
import com.extrade.usermanagement.entities.UserAccount;
import com.extrade.usermanagement.repositories.RoleRepository;
import com.extrade.usermanagement.repositories.UserAccountRepository;
import com.extrade.usermanagement.utilities.RandomGenerator;
import com.extrade.usermanagement.utilities.RoleCodeEnum;
import com.extrade.usermanagement.utilities.UserAccountConstants;
import com.extrade.usermanagement.utilities.UserAccountStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public  class UserManagementServiceImpl implements UserManagmentService {
    private final String TMPL_VERIFY_EMAIL = "confirm-email.html";
    private final UserAccountRepository userAccountRepository;
    private final NotificationManager notificationManager;
    private final RoleRepository roleRepository;
    private final String xtradeCustomerWebLink;

    public UserManagementServiceImpl(UserAccountRepository userAccountRepository,
                                     //BCryptPasswordEncoder bCryptPasswordEncoder,
                                     NotificationManager notificationManager,
                                     RoleRepository roleRepository,
                                     @Value("${eXtrade.customer.weblink}") String xtradeCustomerWebLink) {
        this.userAccountRepository = userAccountRepository;
        this.notificationManager = notificationManager;
        this.roleRepository = roleRepository;
        this.xtradeCustomerWebLink = xtradeCustomerWebLink;
    }


    @Override
    @Transactional(readOnly = true)
    public long countUsersByEmailAddress(String emailAddress) {
        return userAccountRepository.countByEmailAddress(emailAddress);
    }


    @Override
    @Transactional(readOnly = true)
    public long countUserByMobileNo(String mobileNo) {
        return userAccountRepository.countByMobileNo(mobileNo);
    }

    @Override
    @Transactional(readOnly = false)
    public long registerCustomer(UserAccountDto userAccountDto) {
        Role userRole = null;
        LocalDateTime time = null;
        long userAccountId = 0;
        UserAccount userAccount = null;
        String emailVerificationOtpCode = null;
        String mobileNoVerificationOtpCode = null;
        String emailVerificationLink = null;
        MailNotification mailNotification = null;

        time = LocalDateTime.now();
        emailVerificationOtpCode = RandomGenerator.randomAlphaNumericSpecialCharsSequence(8);
        mobileNoVerificationOtpCode = RandomGenerator.randomNumericSequence(6);

        userRole = roleRepository.findByRoleCode(RoleCodeEnum.CUSTOMER.toString());
        log.info("fetched the user role id: {} for the role_cd: {}", userRole.getRoleId(), RoleCodeEnum.CUSTOMER.toString());

        userAccount = new UserAccount();
        userAccount.setFirstName(userAccountDto.getFirstName());
        userAccount.setLastName(userAccountDto.getLastName());
        userAccount.setEmailAddress(userAccountDto.getEmailAddress());
        userAccount.setMobileNo(userAccountDto.getMobileNo());
        userAccount.setPassword(userAccountDto.getPassword());
        userAccount.setGender(userAccountDto.getGender());
        userAccount.setDob(userAccountDto.getDob());
        userAccount.setEmailVerificationOtpCode(emailVerificationOtpCode);
        userAccount.setMobileNoVerificationOtpCode(mobileNoVerificationOtpCode);
        userAccount.setEmailVerificationOtpCodeGeneratedDate(time);
        userAccount.setMobileNoVerificationOtpCodeGeneratedDate(time);
        userAccount.setUserRole(userRole);
        userAccount.setRegisteredDate(LocalDate.now());
        userAccount.setEmailVerificationStatus((short) 0);
        userAccount.setMobileNoVerificationStatus((short) 0);
        userAccount.setLastModifiedBy(UserAccountConstants.SYSTEM_USER);
        userAccount.setLastModifiedDate(time);
        userAccount.setStatus(UserAccountStatusEnum.REGISTERED.getName());

        userAccountId = userAccountRepository.save(userAccount).getUserAccountId();
        log.info("userAccount of email: {} has been saved with userAccountId:{}", userAccount.getEmailAddress(), userAccountId);

        try {

            emailVerificationLink = xtradeCustomerWebLink + "/customer/" + userAccountId + "/"
                    + emailVerificationOtpCode + "/verifyEmail";
            log.debug("email verification link: {} generated", emailVerificationLink);

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("user", userAccountDto.getFirstName() + " " + userAccountDto.getLastName());
            tokens.put("link", emailVerificationLink);

            mailNotification = new MailNotification();
            mailNotification.setFrom("noreply@xtrade.com");
            mailNotification.setTo(new String[]{userAccountDto.getEmailAddress()});
            mailNotification.setSubject("verify your email address");
            mailNotification.setTemplateName(TMPL_VERIFY_EMAIL);
            mailNotification.setTokens(tokens);
            mailNotification.setAttachments(Collections.emptyList());

           // notificationManager.email(mailNotification);
        } catch (Exception e) {
            log.error("error while sending the email to user :{}", userAccountDto.getEmailAddress(), e);
        }


        return userAccountId;
    }
}
package com.extrade.usermanagement.api;

import com.extrade.usermanagement.dto.UserAccountDto;
import com.extrade.usermanagement.service.UserManagmentService;
import com.extrade.usermanagement.utilities.UserAccountConstants;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class UserAccountApiController {
    private final UserManagmentService userManagmentService;

    @PostMapping(value = "/customer", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE})
    @ApiResponses(value = {@ApiResponse(responseCode = "500",
            content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = Long.class))})})
    public ResponseEntity<String> registerCustomer(@RequestBody  UserAccountDto userAccountDto){
        long userAccountId = 0;

        userAccountId = userManagmentService.registerCustomer(userAccountDto);
        log.info("registered a new customer with id: {}", userAccountId);

        return ResponseEntity.ok(String.valueOf(userAccountId));
    }


    @GetMapping(value ="/count/email")
    @ApiResponses(value = {@ApiResponse(responseCode = "500",
            content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "*/*", schema = @Schema(implementation = Long.class))})})
    public ResponseEntity<Long> getNoUsersByEmailAddress(@RequestParam("emailAddress") String emailAddress){
        long c=0;
        c = userManagmentService.countUsersByEmailAddress(emailAddress);
        log.info("no of users found are: {} for the emailAddress: {}", c, emailAddress);
        return ResponseEntity.ok(c);
    }

    @GetMapping(value = "/count/mobileNo")
    @ApiResponses(value = {@ApiResponse(responseCode = "500",
            content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "*/*", schema = @Schema(implementation = Long.class))})})
    public ResponseEntity<Long> getNoOfUsersByMobileNo(@RequestParam("mobileNo") String mobileNo) {
        long c = 0;
        c = userManagmentService.countUserByMobileNo(mobileNo);
        log.info("no of users found are: {} for the mobileNo: {}", c, mobileNo);
        return ResponseEntity.ok(c);
    }
}

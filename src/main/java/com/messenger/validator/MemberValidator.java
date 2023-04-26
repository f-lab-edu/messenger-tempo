package com.messenger.validator;

import com.messenger.dto.member.MemberLoginRequest;
import com.messenger.dto.member.MemberSignupRequest;
import com.messenger.dto.member.MemberUpdateInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Slf4j
@Component
public class MemberValidator implements Validator {

    private static final String ID_PATTERN = "^[a-z]([a-z0-9_]){3,29}$";
    private static final String PASSWORD_PATTERN = "^([a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\-\\.]){6,30}$";
    private static final String NAME_PATTERN = "^.{0,30}$";
    private static final String STATUS_MESSAGE_PATTERN = "^.{0,100}$";

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MemberSignupRequest.class)
                || clazz.equals(MemberLoginRequest.class)
                || clazz.equals(MemberUpdateInfoRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof MemberSignupRequest) {
            MemberSignupRequest request = (MemberSignupRequest) target;
            validateId(request.getId(), errors);
            validatePassword(request.getPassword(), errors);
            if (request.getName() != null) {
                validateName(request.getName(), errors);
            }
        } else if (target instanceof MemberLoginRequest) {
            MemberLoginRequest request = (MemberLoginRequest) target;
            validateId(request.getId(), errors);
            validatePassword(request.getPassword(), errors);
        } else if (target instanceof MemberUpdateInfoRequest) {
            MemberUpdateInfoRequest request = (MemberUpdateInfoRequest) target;
            if (request.getPassword() != null) {
                validatePassword(request.getPassword(), errors);
            }
            if (request.getName() != null) {
                validateName(request.getName(), errors);
            }
            if (request.getStatusMessage() != null) {
                validateStatusMessage(request.getStatusMessage(), errors);
            }
        }
    }

    public static boolean validateId(String id) {
        return !Pattern.matches(ID_PATTERN, id);
    }

    public static boolean validateName(String name) {
        return !Pattern.matches(NAME_PATTERN, name);
    }

    private void validateId(String id, Errors errors) {
        if (validateId(id)) {
            errors.rejectValue("id", "id pattern not match");
        }
    }

    private void validatePassword(String password, Errors errors) {
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            errors.rejectValue("password", "password pattern not match");
        }
    }

    private void validateName(String name, Errors errors) {
        if (validateName(name)) {
            errors.rejectValue("name", "name pattern not match");
        }
    }

    private void validateStatusMessage(String statusMessage, Errors errors) {
        if (!Pattern.matches(STATUS_MESSAGE_PATTERN, statusMessage)) {
            errors.rejectValue("statusMessage", "statusMessage pattern not match");
        }
    }
}

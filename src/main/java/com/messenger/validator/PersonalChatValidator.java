package com.messenger.validator;

import com.messenger.dto.chat.SendPersonalChatRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonalChatValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SendPersonalChatRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SendPersonalChatRequest request = (SendPersonalChatRequest) target;
        String userId = request.getReceiverUserId();
        String content = request.getContent();
        if (!MemberValidator.validateId(userId)) {
            errors.rejectValue("id", "id pattern not match");
        }
        if (content.length() > 5000) {
            errors.rejectValue("content", "string length is too long");
        }
    }
}

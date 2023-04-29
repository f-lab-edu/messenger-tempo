package com.messenger.validator;

import com.messenger.dto.chat.MakeNewGroupRequest;
import com.messenger.dto.chat.SendGroupChatRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class GroupChatValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SendGroupChatRequest.class)
                || clazz.equals(MakeNewGroupRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof SendGroupChatRequest) {
            SendGroupChatRequest request = (SendGroupChatRequest) target;
            String content =  request.getContent();
            if (content.length() > 5000) {
                errors.rejectValue("content", "string length is too long");
            }
        } else if (target instanceof MakeNewGroupRequest) {
            MakeNewGroupRequest request = (MakeNewGroupRequest) target;
            List<String> memberList = request.getMemberList();
            for (String memberId : memberList) {
                if (!MemberValidator.validateId(memberId)) {
                    errors.rejectValue("memberList", "member id pattern is not match");
                    break;
                }
            }
        }
    }
}

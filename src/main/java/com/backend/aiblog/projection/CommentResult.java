package com.backend.aiblog.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CommentResult {
    @Value("#{target.id}")
    String getId();

    @Value("#{target.body}")
    String getBody();
}

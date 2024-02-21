package com.backend.aiblog.mapper;

import com.backend.aiblog.dto.response.CommentResponse;
import com.backend.aiblog.projection.CommentResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentResponseMapper {

    List<CommentResponse> mapperCommentResponse(List<CommentResult> commentResultList);
}

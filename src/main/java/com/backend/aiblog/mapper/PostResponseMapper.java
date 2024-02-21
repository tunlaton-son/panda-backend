package com.backend.aiblog.mapper;

import com.backend.aiblog.dto.response.PostResponse;
import com.backend.aiblog.entity.Post;
import com.backend.aiblog.projection.PostResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostResponseMapper {
    PostResponse mapperPostResponse(PostResult postResult);
}

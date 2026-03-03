package com.chatpg.mapper

import com.chatpg.domain.poll.Poll
import com.chatpg.dto.poll.PollDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PollMapper {

    @Mapping(target = "votes", expression = "java(poll.getVotes().size())")
    fun toDto(poll: Poll): PollDto

}
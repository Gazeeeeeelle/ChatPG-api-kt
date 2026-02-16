package com.yourRPG.chatPG.mapper

import com.yourRPG.chatPG.domain.poll.Poll
import com.yourRPG.chatPG.dto.poll.PollDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PollMapper {

    @Mapping(target = "votes", expression = "java(poll.getVotes().size())")
    fun toDto(poll: Poll): PollDto

}
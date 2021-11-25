package InTechs.InTechs.chat.payload.response;

import InTechs.InTechs.chat.entity.ChatType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {

    private final String id;

    private final String message;

    private final SenderResponse sender;

    private final Boolean isDelete;

    private final Boolean notice;

    private final ChatType chatType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time;

}

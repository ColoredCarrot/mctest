package info.voidev.mcproto.api

import java.util.UUID

data class ChatMessage(val message: ChatComponent, val sender: UUID)

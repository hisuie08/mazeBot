package me.nashiroaoi.simplemaze

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Command {
    private fun checkMention(event : MessageReceivedEvent):Boolean{
        return (event.message.mentionedUsers.count() == 1
            && event.message.mentionedUsers.contains(event.jda.selfUser))
    }
    
    fun run(event : MessageReceivedEvent){
        if(!checkMention(event)){return}
        val n = Regex("(create|c)( +?)([0-9]+)").find(event.message.contentDisplay)?.groups?.get(3)?.value
        
    }
}
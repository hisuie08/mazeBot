package me.nashiroaoi.simplemaze

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent


class BotClient:ListenerAdapter(){
    private lateinit var jda: JDA
    fun main(token:String){
        jda = JDABuilder.createLight(token,
            GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(this)
            .build()
    }
    
    override fun onReady(event : ReadyEvent) {
        event.jda.presence.activity = Activity.playing("迷路ゲーム")
    }
    
    override fun onMessageReceived(event : MessageReceivedEvent) {
        Command().run(event)
    }
    
}

fun main(){
    val token = System.getenv("Discord_Bot_Token")
    //val bot = BotClient()
    //bot.main("NTY3OTg1ODg4MjE1MTcxMDcy.XLbgFA.lVM91h87qKge38YeZb5Pbyrw91E")
    
    
    
}
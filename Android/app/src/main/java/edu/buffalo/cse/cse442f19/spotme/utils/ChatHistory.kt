package edu.buffalo.cse.cse442f19.spotme.utils

class ChatHistory {

    var listener: ChatChangeListener? = null;
    var messages: ArrayList<ChatMessage> = arrayListOf()

    fun addMessage(message: ChatMessage) {

        messages.add(message);
        listener?.onChange(message)
    }

    fun getSize(): Int {

        return messages.size
    }

    interface ChatChangeListener {

        fun onChange(message: ChatMessage);
    }

    class ChatMessage {

        var self: Boolean = false;
        var message: String;
        var time: String;

        constructor(self: Boolean, message: String, time: String) {

            this.self = self;
            this.message = message;
            this.time = time;
        }
    }
}
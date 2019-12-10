package com.example.pickmeup.Messages.Chat;

class Chat {
    private String chatName;
    private String lastChat;
    private int chatImage;
    private int chatId;

    /**
     * @param chatName name of chat
     * @param lastChat last message in chat
     * @param chatImage image of chat (sport type)
     * @param chatId id of chat for filtering
     */
    Chat(String chatName, String lastChat, int chatImage, int chatId) {
        this.chatName = chatName;
        this.lastChat = lastChat;
        this.chatImage = chatImage;
        this.chatId = chatId;
    }

    /**
     * @return name of the chat
     */
    String getChatName() {
        return chatName;
    }

    /**
     * @return last message in the chat
     */
    String getLastChat() {
        return lastChat;
    }

    /**
     * @return image of the chat
     */
    int getChatImage() {
        return chatImage;
    }

    /**
     * @return id of the chat
     */
    int getChatId() {
        return chatId;
    }
}

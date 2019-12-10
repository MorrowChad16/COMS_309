package pickmeup.webSockets;

import java.util.Date;

public class GameChatMessage {
    private String message;
    private String messengerName;
    private int messengerImage;
    private Date timestamp;
    private Integer chatId;

    public GameChatMessage(Integer chatId, String message, String messengerName, int messengerImage, Date timestamp) {
        this.chatId = chatId;
        this.message = message;
        this.messengerName = messengerName;
        this.messengerImage = messengerImage;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getMessengerName() {
        return messengerName;
    }

    public int getMessengerImage() {
        return messengerImage;
    }

    public Date getTimestamp(){
        return timestamp;
    }
    
    public Integer getChatId(){
        return chatId;
    }

    public String toFormattedString() {
        return "\nGameChatMessage [\nchatId=" + chatId + ", message=" + message + ", \nmessengerImage=" + messengerImage
                + ", messengerName=" + messengerName + ", \ntimestamp=" + timestamp + "\n]";
    }

    @Override
    public String toString() {
        return "{\"chatId\":" + chatId + ",\"message\":" + "\"" + message  + "\"" + ",\"messengerImage\":" + messengerImage
                + ",\"messengerName\":" + "\"" + messengerName + "\"" + ",\"timestamp\":" + "\"" + timestamp + "\"" + "}";
    }

}
package com.example.pickmeup.Messages.Messages;

import android.annotation.SuppressLint;

import androidx.annotation.VisibleForTesting;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Message {
    private String message;
    private String messengerName;
    private int messengerImage;
    private Date timestamp;
    private Integer chatId;
    private boolean sent;

    public static String simpleDateFormatJSON = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * @return string formatted date
     */
    @SuppressLint("SimpleDateFormat")
    @VisibleForTesting
    String getJsonTime(){
        if(timestamp != null){
            return (new SimpleDateFormat(simpleDateFormatJSON)).format(timestamp);
        }
        else{
            return null;
        }
    }

    /**
     * @param date of the message
     * @return string of the message time
     */
    @SuppressLint("SimpleDateFormat")
    private static String jsonTime(Date date){
        if(date != null){
            return (new SimpleDateFormat(simpleDateFormatJSON)).format(date);
        }
        else{
            return null;
        }
    }

    /**
     * @param sent sets the value to sent for when the user sends a text
     */
    public void setSent(Boolean sent){
        this.sent = sent;
    }

    public boolean getSent(){
        return sent;
    }

    /**
     * @param date to be formatted
     * @return string of formatted date
     */
    @SuppressLint("SimpleDateFormat")
    private static String simpleTime(Date date){
        if(date != null){
            return  (new SimpleDateFormat("hh:mm")).format(date);
        } else{
            return null;
        }

    }

    /**
     * @param chatId id linked to the chat
     * @param message sent from the user
     * @param messengerName name of the person who sent the message
     * @param messengerImage image of user
     * @param timeStamp time when the message was sent
     */
    Message(Integer chatId, String message, String messengerName, int messengerImage, Date timeStamp) {
        this.message = message;
        this.messengerName = messengerName;
        this.messengerImage = messengerImage;
        this.timestamp = timeStamp;
        this.chatId = chatId;
    }

    /**
     * @return message sent by user
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return name of the messenger
     */
    String getMessengerName() {
        return messengerName;
    }

    /**
     * @return the int image value of the messenger
     */
    int getMessengerImage() {
        return messengerImage;
    }

    /**
     * @return time when the user sent the message
     */
    String getTimestamp(){
        return simpleTime(timestamp);
    }

    /**
     * @return chat id linked to the chat
     */
    public Integer getChatId(){
        return chatId;
    }

    /**
     * @return a string formatted to a string style
     */
    public String toFormattedString() {
        return "\nGameChatMessage [\nchatId=" + chatId + ", message=" + message + ", \nmessengerImage=" + messengerImage
                + ", messengerName=" + messengerName + ", \ntimeStamp=" + timestamp + "\n]";
    }

    /**
     * @return a string formatted to a string style
     */
    @NotNull
    @Override
    public String toString() { //json format
        return "{\"chatId\":" + chatId + ",\"message\":" + "\"" + message  + "\"" + ",\"messengerImage\":" + messengerImage
                + ",\"messengerName\":" + "\"" + messengerName + "\"" + ",\"timestamp\":" + "\"" + jsonTime(timestamp )+ "\"" + "}";
    }

    /**
     * @param o object to compare to
     * @return true if item is equal
     */
    @Override
    public boolean equals(Object o){
        if(o == null) return false;

        if(o instanceof Message){
            Message other = (Message) o;
            return this.chatId.equals(other.chatId) && this.message.equals(other.message)
                    && this.messengerName.equals(other.messengerName)
                    && Objects.equals(this.getJsonTime(), other.getJsonTime());
        }

        return false;
    }

    /**
     * @return hashed code
     */
    @Override
    public int hashCode() {
        return this.timestamp.hashCode() + this.messengerName.hashCode() + this.message.hashCode() + this.chatId.hashCode();
    }
}

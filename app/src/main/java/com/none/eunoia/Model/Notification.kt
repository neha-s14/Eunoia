package com.none.eunoia.Model

class Notification {
    private var donator:String=""
    private var key:String=""
    private var notificationId:String=""
    private var postid:String=""
    private var reciever:String=""
    private var status:String=""
constructor()
    constructor(
        donator: String,
        key: String,
        notificationId: String,
        postid: String,
        reciever: String,
        status: String
    ) {
        this.donator = donator
        this.key = key
        this.notificationId = notificationId
        this.postid = postid
        this.reciever = reciever
        this.status = status
    }
    fun getDonator():String{
        return donator
    }
    fun getKey():String{
        return key
    }
    fun getNotificationId():String{
        return notificationId
    }
    fun getPostid():String{
        return postid
    }
    fun getReciever():String{
        return reciever
    }
    fun getStatus():String{
        return status
    }
    fun setDonator(donator: String){
        this.donator=donator
    }
    fun setKey(key:String)
    {
        this.key=key
    }
    fun setNotificationId(notificationId: String){
        this.notificationId=notificationId
    }
    fun setPostid(postid: String)
    {
        this.postid=postid
    }
    fun setReciever(reciever: String){
        this.reciever=reciever
    }
    fun setStatus(status:String){
        this.status=status
    }

}
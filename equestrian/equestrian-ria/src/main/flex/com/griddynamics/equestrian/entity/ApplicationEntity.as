package com.griddynamics.equestrian.entity {
[RemoteClass(alias="com.griddynamics.equestrian.model.Application")]
[Bindable]
public class ApplicationEntity
{
    public var schedulerStatus:Boolean;
    public var date:Date;
    public var workers:String;
    public var applicationStatus:String;
    public var parsing:String;
    public var returning:String;
    public var time:String;
    public var ip:String;
    public var traf:String;
    public var nodeIp:String;

    public function ApplicationEntity() {
    }
}
}
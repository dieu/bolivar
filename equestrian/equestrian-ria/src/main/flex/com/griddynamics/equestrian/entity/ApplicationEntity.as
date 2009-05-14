package com.griddynamics.equestrian.entity {
[RemoteClass(alias="com.griddynamics.equestrian.model.Application")]
[Bindable]
public class ApplicationEntity
{
    public var serverStatus:Boolean;
    public var workerStatus:Boolean;
    public var scheluderStatus:Boolean;
    public var applicationStatus:String;
    public var time:String;
    public var ip:String;
    public var traf:String;


    public function ApplicationEntity() {
    }
}
}
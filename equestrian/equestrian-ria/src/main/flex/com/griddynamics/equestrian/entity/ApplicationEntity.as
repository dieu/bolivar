package com.griddynamics.equestrian.entity {
[RemoteClass(alias="com.griddynamics.equestrian.model.Application")]
[Bindable]
public class ApplicationEntity
{
    public var scheluderStatus:Boolean;
    public var nWorkers:int;
    public var applicationStatus:String;
    public var time:String;
    public var ip:String;
    public var traf:String;


    public function ApplicationEntity() {
    }
}
}
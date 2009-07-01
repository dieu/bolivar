package com.griddynamics.equestrian.helpers {
    import mx.controls.Label;
    import mx.controls.dataGridClasses.*;

	public class CustomRole extends Label {
	 override public function set data(value:Object):void
	 {
	  	if(value != null)
	  	{
	  	   super.data = value;
	  	   if(value[DataGridListData(listData).dataField] == 'M') {
	 		      setStyle("color", 0xFF0000);
	 		 }
	 		 else {
	 		     setStyle("color", 0x000000);
	 		 }
	  	}
     }
  }
}
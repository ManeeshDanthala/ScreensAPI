package org.maneesh.udaan.uproject.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class screens {
	private String name;
	private HashMap<String,ArrayList<Long>> seatInfo = new HashMap<>();
	public void setHashMap(HashMap<String,ArrayList<Long>> seatInfo) {
		this.seatInfo = seatInfo;
	}
	public String getName() {
		return name;
	}
	public screens() {
		
	}
	public screens(String name) {
		this.name = name;
	}
	public screens(String name,HashMap<String,ArrayList<Long>> seatInfo) {
		this.name = name;
		this.seatInfo = seatInfo;
	}
	public HashMap<String,ArrayList<Long>> getHashMap(){
		return seatInfo;
	}
	/*public void objstring() {
		if(seatInfo==null) {
			//System.out.println("seatinfo is null");
		}
		else {
			System.out.println("-----------------------------------------------");
			//System.out.println(seatInfo);
			for(Map.Entry<String, ArrayList<Long>> m:seatInfo.entrySet()) {
				//System.out.print(m.getKey()+"  ");
				for(int i=0;i<m.getValue().size();i++) {
					//System.out.print(m.getValue().get(i)+"  ");
				}
				//System.out.println();
			}
		}
	}*/
}

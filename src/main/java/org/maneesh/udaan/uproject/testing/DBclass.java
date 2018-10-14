package org.maneesh.udaan.uproject.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBclass {
	private static ArrayList<String> screenNames = new ArrayList<>();
	private static ArrayList<screens> screenDetails = new ArrayList<>();
	private static ArrayList<screens> booked = new ArrayList<>();

	public static Response reserve(String scrnName, String seats) throws ParseException {//To reserve tickts
		int index = screenNames.indexOf(scrnName);
		if(index==-1) {//if not valid url
			return Response.status(Response.Status.BAD_REQUEST).entity("Please enter valid url").build();
		}
		JSONParser parser = new JSONParser();
		JSONObject jo = (JSONObject) parser.parse(seats);
		if(jo==null) {//if not valid url
			return Response.status(Response.Status.BAD_REQUEST).entity("Please enter valid url").build();
		}
		JSONObject sender = (JSONObject) jo.get("seats");
		HashMap<String, ArrayList<Long>> hm = new HashMap<>();
		if(sender==null) {//if not valid url
			return Response.status(Response.Status.BAD_REQUEST).entity("please enter valid request").build();
		}
		for (Iterator iterator = sender.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONArray jarray = (JSONArray) sender.get(key);
			if(jarray==null) {//if not valid url
				return Response.status(Response.Status.BAD_REQUEST).entity("please enter valid request").build();
			}
			Iterator<Long> itr = jarray.iterator();
			ArrayList<Long> temp;
			Long Maintemp;
			if (booked.get(index).getHashMap().get(key) != null) {
				temp = booked.get(index).getHashMap().get(key);
			} else {
				temp = new ArrayList<>();
			}
			if (screenDetails.get(index).getHashMap().get(key) == null) {
				return Response.status(Response.Status.BAD_REQUEST).entity("select valid seat numbers").build();
			} else {
				Maintemp = screenDetails.get(index).getHashMap().get(key).get(0);
			}
			ArrayList<Long> al = new ArrayList<Long>();
			while (itr.hasNext()) {
				Long ll = itr.next();
				System.out.print(ll + "  ");
				if (temp.contains(ll)) {
					return Response.status(Response.Status.CONFLICT)
							.entity("seats already booked. Please try booking available seats").build();
				}
				if (Maintemp <= ll) {
					return Response.status(Response.Status.BAD_REQUEST).entity("select valid seat numbers").build();
				}
				al.add(ll);
			}
			hm.put(key, al);
		}
		if (booked.get(index).getHashMap() == null) {
			booked.get(index).setHashMap(hm);
		} else {
			Map<String, ArrayList<Long>> h = booked.get(index).getHashMap();
			for (Map.Entry<String, ArrayList<Long>> map : hm.entrySet()) {
				ArrayList<Long> tt;
				if (h.get(map.getKey()) != null)
					tt = h.get(map.getKey());
				else
					tt = new ArrayList<>();
				tt.addAll(hm.get(map.getKey()));
				h.put(map.getKey(), tt);
			}

		}
		//booked.get(index).objstring();
		return Response.status(Response.Status.OK).entity("Successfully booked seats").build();
	}

	@SuppressWarnings("deprecation")
	public static JSONObject getAllAvailableSeats(String scrnName) {//To return all available seats
		JSONObject finalres = new JSONObject();
		HashMap<String, ArrayList<Long>> avail = new HashMap<>();
		int indx = screenNames.indexOf(scrnName);
		if (indx==-1 || screenDetails.get(indx) == null) {//if not valid url
			return null;
		}
		HashMap<String, ArrayList<Long>> tempal = screenDetails.get(indx).getHashMap();
		JSONObject ress = new JSONObject();
		for (Map.Entry<String, ArrayList<Long>> map : tempal.entrySet()) {
			ArrayList<Long> tem = new ArrayList<>();
			if (booked.get(indx).getHashMap().get(map.getKey()) == null) {
				for (int i = 0; i < map.getValue().get(0); i++) {
					tem.add((long) i);
				}
				ress.put(""+map.getKey(), tem);
				continue;
			} else {
				ArrayList<Long> t = booked.get(indx).getHashMap().get(map.getKey());
				for (int i = 0; i < map.getValue().get(0); i++) {
					if (!t.contains((long)i)) {
						tem.add((long) i);
					}
				}
				System.out.println("final is "+tem);
				ress.put(""+map.getKey(), tem);
			}

		}
		finalres.put("seats",ress);
		return finalres;
	}

	public static boolean storeScreen(screens scrn) {//For storing screen that is registering screen
		if(screenNames.contains(scrn.getName())) {
			return false;
		}
		screenNames.add(scrn.getName());
		screenDetails.add(scrn);
		booked.add(new screens(scrn.getName()));
		return true;
	}

	public static JSONObject getPossibleSeats(String scrnName,String row,long numseats,long seatnum) {//To get possible seats
		int idx = screenNames.indexOf(scrnName);
		if(idx==-1) {//if not valid url
			return null;
		}
		ArrayList<Long> range = screenDetails.get(idx).getHashMap().get(row);
		if((range.get(range.size()-1)+1)!=range.get(0)) {
			range.add(range.get(range.size()-1)+1);
			range.add(range.get(0)-1);
		}
		ArrayList<Long> window = new ArrayList<>();
		ArrayList<Long> availTcks = getAvailableSeatsRow(scrnName,row);
		long min=Long.MAX_VALUE, max = Long.MIN_VALUE;
		if(numseats>availTcks.size() || range.size()<3 || seatnum>range.get(0)) {
			return null;
		}
		for(int j=1;j<range.size();j=j+2) {//min and max range of aisle seats
			if(seatnum>=range.get(j) && seatnum<=range.get(j+1)) {
				min = range.get(j);
				max = range.get(j+1);
			}
		}
		int ct=0;
		long i=0;
		for(;ct<numseats-1 && i<availTcks.size();i++) {
			if(availTcks.get((int)i)>=min && availTcks.get((int)i)<=max) {
				window.add(availTcks.get((int)i));
				ct++;
			}
			if(availTcks.get((int)i)>max) {
				break;
			}
		}
		if(window.size()+1<numseats) {
			return null;
		}
		
		for(;i<availTcks.size() && availTcks.get((int)i)<=max;i++) {
			window.add(availTcks.get((int)i));
			if(!window.contains(seatnum) || (window.get(window.size()-1)-window.get(0)+1)!=numseats) {
				window.remove(0);
				continue;
			}
			else {
				JSONObject jobject = new JSONObject();
				HashMap<String,ArrayList<Long>> hj = new HashMap<>();
				hj.put(row,window);
				jobject.put("availableSeats",hj);
				return jobject;
			}
		}
		return null;
	}
	
	public static ArrayList<Long> getAvailableSeatsRow(String scrnName,String row){//To get available seats for a purticular row
		int idx = screenNames.indexOf(scrnName);
		if(idx==-1) {//if not valid url
			return null;
		}
		ArrayList<Long> bookedSeats;
		if(booked.get(idx).getHashMap().get(row)==null) {
			bookedSeats = new ArrayList<>();
		}
		else {
			bookedSeats  = new ArrayList<>(booked.get(idx).getHashMap().get(row));
		}
		long maxVal = screenDetails.get(idx).getHashMap().get(row).get(0);
		ArrayList<Long> totalSeats = new ArrayList<>();
		for(long i=0;i<maxVal;i++) {
			if(!bookedSeats.contains(i))
				totalSeats.add(i);
		}
		totalSeats.removeAll(bookedSeats);
		return totalSeats;//final result
	}
}

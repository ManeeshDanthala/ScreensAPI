package org.maneesh.udaan.uproject.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tomcat.jni.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/screens")
public class test {

	@Path("/{screenName}/reserve")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reserveTicket(@PathParam("screenName") String screenName, String seats) throws ParseException {
		return DBclass.reserve(screenName, seats);//For reserving movie seats
	}

	@Path("/{screenName}/seats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object getAvailableSeats(@QueryParam("status") String statusOfTcks, @PathParam("screenName") String scrnName,
			@QueryParam("numSeats") long numSeats, @QueryParam("choice") String choice) {
		//To get available seats and possible seats
		if (statusOfTcks != null && choice == null) {//to get available seats
			if (!statusOfTcks.equals("unreserved")) {//if not valid url
				return Response.status(Response.Status.BAD_REQUEST).entity("please enter valid url").build();
			}
			JSONObject res = DBclass.getAllAvailableSeats(scrnName);
			if (res == null) {//if not valid url
				return Response.status(Response.Status.NOT_FOUND).entity("Please select valid registered theatre")
						.build();
			}
			return res;
		} else if (statusOfTcks == null && choice != null) {//to get possible optimal seats
			String row = choice.charAt(0) + "";
			long seatNo = Long.parseLong(choice.substring(1));
			JSONObject jobj = DBclass.getPossibleSeats(scrnName, row, numSeats, seatNo);
			if (jobj == null) {//if not valid url
				return Response.status(Response.Status.NOT_FOUND).entity("Booking with given information is not possible")
						.build();
			} else {
				return jobj;
			}
		} else {//if url is wrong
			return Response.status(Response.Status.BAD_REQUEST).entity("Please give valid url").build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Object addScreen(String obj) throws ParseException {//For adding screen that is registering theatre
		JSONParser parser = new JSONParser();//parsing
		JSONObject jo = (JSONObject) parser.parse(obj);
		String name = (String) jo.get("name");
		if (name == null) {//if not valid url
			return Response.status(Response.Status.BAD_REQUEST).entity("enter valid request").build();
		}
		JSONObject sender = (JSONObject) jo.get("seatInfo");
		if (sender == null) {//if not valid url
			return Response.status(Response.Status.BAD_REQUEST).entity("enter valid request").build();
		}
		HashMap<String, ArrayList<Long>> hm = new HashMap<>();
		for (Iterator iterator = sender.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject js = (JSONObject) sender.get(key);
			ArrayList<Long> all = new ArrayList<>();
			if (js == null) {//if not valid url
				return Response.status(Response.Status.BAD_REQUEST).entity("enter valid request").build();
			}
			JSONArray jarray = (JSONArray) js.get("aisleSeats");
			if (jarray == null) {//if not valid url
				return Response.status(Response.Status.BAD_REQUEST).entity("enter valid request").build();
			}
			try {
				all.add(Long.parseLong(js.get("numberOfSeats") + ""));
			}
			catch(Exception e){
				return Response.status(Response.Status.BAD_REQUEST).entity("enter valid request").build();
			}
			
			Iterator<Long> itr = jarray.iterator();
			while (itr.hasNext()) {
				Long ll = itr.next();
				all.add(ll);
			}
			hm.put(key + "", all);
		}
		if (DBclass.storeScreen(new screens(name, hm)))
			return Response.status(200).entity("Successfully added theatre").build();
		else//any error occured
			return Response.status(Response.Status.BAD_REQUEST).entity("Theatre already exists").build();
			
	}
}

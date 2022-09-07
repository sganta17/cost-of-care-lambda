package com.external;

import java.util.UUID;

import com.pojo.Leads;

public class SfmcTest {
	public static void main(String[] args){
        final UUID uuid = UUID.randomUUID();
		Leads lead = new Leads();
		lead.leadID = uuid.toString();
		lead.leadName = "Test";
		lead.leadEmail = "sganta7@massmutual.com";
		Sfmc.postSFMCData(lead);
	}
}
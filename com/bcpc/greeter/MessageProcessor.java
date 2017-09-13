package com.bcpc.greeter;

import com.bcpc.greeter.db.MessagingDao;
import com.google.inject.Inject;

public class MessageProcessor {

	
	private final MessagingDao dao;
	
	@Inject
	MessageProcessor(PropertyManager pMgr, MessagingDao dao)
	{
		this.dao = dao;
	}
	
	public void processMessage(String body, String fromNumber) throws Exception
	{
		String emailId = null;
		String name = null;
		
		String[] details = MessageUtil.extractFromBody(body);

		if (details != null) {
			if (details.length != 2) {
				throw new Exception("INVALID INPUT FORMAT");
			}
		}
		
		if(MessageUtil.isValidEmail(details[0]))
		{
			emailId = details[0];
			name = details[1];
		}
		else if(MessageUtil.isValidEmail(details[1]))
		{
			emailId = details[1];
			name = details[0];
		}
		else
		{
			throw new Exception("INVALID EMAIL FORMAT");
		}
		
		if(dao.checkIfmessageInDb(fromNumber))
		{
			throw new Exception("NUMBER ALREADY REGISTERED");
		}
		
		dao.insertMessageToDb(fromNumber, name, emailId);
	}
}

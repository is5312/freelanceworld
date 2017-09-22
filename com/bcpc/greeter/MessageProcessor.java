package com.bcpc.greeter;

import java.util.Stack;

import com.bcpc.greeter.db.MessagingDao;
import com.bcpc.greeter.exceptions.GreeterException;
import com.bcpc.greeter.exceptions.GreeterException.ErrorCode;
import com.google.inject.Inject;

public class MessageProcessor {

	private final MessagingDao dao;

	@Inject
	MessageProcessor(PropertyManager pMgr, MessagingDao dao) {
		this.dao = dao;
	}

	public void processMessage(String body, String fromNumber) throws Exception {
		String emailId = null;
		StringBuffer name = new StringBuffer();

		String[] details = MessageUtil.extractFromBody(body);

		if (details != null) {
			if (details.length < 2) {
				throw new GreeterException("", ErrorCode.FORMAT_ERROR);
			}
		}

		for (String b : details) {
			if (MessageUtil.isValidEmail(b)) {
				emailId = b;
			} else {
				name.append(b).append(" ");
			}
		}

		if (isMessageInDb(fromNumber)) {
			throw new GreeterException("", ErrorCode.ALREADY_REGISTERED_ERROR);
		}

		dao.insertMessageToDb(fromNumber, name.toString(), emailId);
	}

	private boolean isMessageInDb(String fromNumber) {
		MessageStoreBean bean = dao.checkIfmessageInDb(fromNumber);
		if (null == bean) {
			return false;
		}

		if (bean.getErrorCd() == ErrorCode.FORMAT_ERROR.getId()) {

		}

		return true;
	}
}

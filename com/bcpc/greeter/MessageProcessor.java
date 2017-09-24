package com.bcpc.greeter;

import com.bcpc.greeter.db.MessagingDao;
import com.bcpc.greeter.exceptions.GreeterException;
import com.bcpc.greeter.exceptions.GreeterException.ErrorCode;
import com.bcpc.greeter.exceptions.GreeterUnresolvedException;
import com.google.inject.Inject;
import com.mysql.jdbc.StringUtils;
import static com.bcpc.greeter.Constants.MAX_RETRY_CNT_DEF;

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

		MessageStoreBean bean = dao.checkIfmessageInDb(fromNumber);
		String errFlag = "N";

		// the message body is invalid
		if (!isValidMessageDetails(details)) {
			// number doesnt exist in db
			if (null == bean) {
				dao.insertMessageToDb(fromNumber, name.toString(), emailId, ErrorCode.FORMAT_ERROR);
				throw new GreeterException("", ErrorCode.FORMAT_ERROR);
			} else {
				// number exists in db - validate
				validateExistingDetailsInDb(bean, errFlag, fromNumber, emailId, name.toString());
			}
		}

		for (String b : details) {
			if (MessageUtil.isValidEmail(b)) {
				emailId = b;
			} else {
				name.append(b).append(" ");
			}
		}

		// message exists in DB
		if (null != bean ) {
			validateExistingDetailsInDb(bean, errFlag, fromNumber, emailId, name.toString());
		} else {
			// message doesn't exist in DB
			if (StringUtils.isNullOrEmpty(emailId) || StringUtils.isNullOrEmpty(name.toString())) {
				// The message format is invalid
				dao.insertMessageToDb(fromNumber, name.toString(), emailId, ErrorCode.FORMAT_ERROR);
				throw new GreeterException("", ErrorCode.FORMAT_ERROR);
			}

			dao.insertMessageToDb(fromNumber, name.toString(), emailId, null);
		}
	}

	private void validateExistingDetailsInDb(MessageStoreBean bean, String errFlag, String fromNumber, String emailId,
			String name) throws GreeterException, GreeterUnresolvedException {
		// There is an active error which needs to be resolved
		if (bean.getErrorFlag() != null && bean.getErrorFlag().equalsIgnoreCase("Y") && bean.getErrorCd() == ErrorCode.FORMAT_ERROR.getId()) {
			if (bean.getRetryCnt() > MAX_RETRY_CNT_DEF) {
				throw new GreeterException("", ErrorCode.EXCEEDED_MAX_RETRY_CNT);
			}
			if (StringUtils.isNullOrEmpty(emailId) || StringUtils.isNullOrEmpty(name)) {
				throw new GreeterException("", ErrorCode.FORMAT_ERROR);
			}
			dao.updateMessageToDb(fromNumber, name, emailId, errFlag, bean.getRetryCnt() + 1);
		} else {
			if (bean.getRetryCnt() > MAX_RETRY_CNT_DEF) {
				throw new GreeterException("", ErrorCode.EXCEEDED_MAX_RETRY_CNT);
			}
			if (null != bean.getErrorFlag() && bean.getErrorFlag().equalsIgnoreCase("Y")) {
				dao.updateMessageToDb(fromNumber, name, emailId, errFlag, bean.getRetryCnt() + 1);
			}
			else
			{
				dao.updateMessageCntToDb(fromNumber, bean.getRetryCnt() + 1);
			}
			// There is no active error and the number is already present
			throw new GreeterException("", ErrorCode.ALREADY_REGISTERED_ERROR);
		}
	}

	private boolean isValidMessageDetails(String[] details) {
		boolean isValid = true;

		if (details != null) {
			if (details.length < 2) {
				isValid = false;
			}
		}
		return isValid;
	}

}

package com.bcpc.greeter.exceptions;

public class GreeterException extends Exception {

	private static final long serialVersionUID = -4292624102805187153L;

	public enum ErrorCode {
		FORMAT_ERROR(1001), ALREADY_REGISTERED_ERROR(1002), EXCEEDED_MAX_RETRY_CNT(9999);

		private int id;

		private ErrorCode(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}
	}

	public GreeterException(String message,  ErrorCode code) throws GreeterUnresolvedException {
		super(getResolvedExceptionMessage(message, code));
	}

	private static String getResolvedExceptionMessage(String message, ErrorCode code) throws GreeterUnresolvedException {
		String resolvedMessage = "";
		switch (code) {
		case FORMAT_ERROR:
			resolvedMessage = "Please reply in the format <NAME><SPACE><EMAIL_ID> . eg: Sam sam@yahoo.com";
			break;
		case ALREADY_REGISTERED_ERROR:
			resolvedMessage = "Thank you, your number is already registered.";
			break;
		case EXCEEDED_MAX_RETRY_CNT:
			resolvedMessage = "This number has exceeded the max retries";
			throw new GreeterUnresolvedException(resolvedMessage, ErrorCode.EXCEEDED_MAX_RETRY_CNT);
		default:
			throw new GreeterUnresolvedException("Unresolved Exception", null);
		}

		return String.format("%s %s", resolvedMessage, message);
	}

}

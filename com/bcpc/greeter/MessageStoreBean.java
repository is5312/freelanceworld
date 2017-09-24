package com.bcpc.greeter;

public class MessageStoreBean {

	private String pNumber;
	private String name;
	private String email;
	private String errorFlag;
	private String errorMsg;
	private int errorCd;
	private int retryCnt;
	
	
	public int getRetryCnt() {
		return retryCnt;
	}
	public void setRetryCnt(int retryCnt) {
		this.retryCnt = retryCnt;
	}
	public int getErrorCd() {
		return errorCd;
	}
	public void setErrorCd(int errorCd) {
		this.errorCd = errorCd;
	}
	public String getpNumber() {
		return pNumber;
	}
	public void setpNumber(String pNumber) {
		this.pNumber = pNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@Override
	public String toString() {
		return "MessageStoreBean [pNumber=" + pNumber + ", name=" + name + ", email=" + email + ", errorFlag="
				+ errorFlag + ", errorMsg=" + errorMsg + "]";
	}
}

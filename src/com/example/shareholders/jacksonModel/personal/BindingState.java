package com.example.shareholders.jacksonModel.personal;

/**
 * 个人中心接口 3.4 查看绑定状态
 * 
 * @author warren
 * 
 */
public class BindingState {
	private String bindPhone;
	private String bindEmail;
	private boolean bindQQ;
	private boolean bindWeixin;
	private boolean bindWeibo;

	public String getBindPhone() {
		return bindPhone;
	}

	public void setBindPhone(String bindPhone) {
		this.bindPhone = bindPhone;
	}

	public String getBindEmail() {
		return bindEmail;
	}

	public void setBindEmail(String bindEmail) {
		this.bindEmail = bindEmail;
	}

	public boolean isBindQQ() {
		return bindQQ;
	}

	public void setBindQQ(boolean bindQQ) {
		this.bindQQ = bindQQ;
	}

	public boolean isBindWeixin() {
		return bindWeixin;
	}

	public void setBindWeixin(boolean bindWeixin) {
		this.bindWeixin = bindWeixin;
	}

	public boolean isBindWeibo() {
		return bindWeibo;
	}

	public void setBindWeibo(boolean bindWeibo) {
		this.bindWeibo = bindWeibo;
	}

	@Override
	public String toString() {
		return "BindingState [bindPhone=" + bindPhone + ", bindEmail="
				+ bindEmail + ", bindQQ=" + bindQQ + ", bindWeixin="
				+ bindWeixin + ", bindWeibo=" + bindWeibo + "]";
	}

}

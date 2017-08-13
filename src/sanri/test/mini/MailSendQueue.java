package sanri.test.mini;

import java.io.Serializable;

/**
 * 对于发邮件失败的任务,将信息写入这张表,
定时任务就会扫描这张表,将未发送成功的邮件进行再次发送
 */
public class MailSendQueue implements Serializable,Comparable<MailSendQueue>{
	private Integer id;
	// 0 已发送成功,1:未发送成功
	private int sendFlag;
	// 邮件内容,不能超过 500 个字符
	private String content;
	// 邮件标题
	private String subject;
	// 附件名称,使用分号分隔,和附件一一对应
	private String attachmentNames;
	// 附件路径列表,用分号分隔,超过 3 个附件请打包
	private String attachments;
	// 抄送人,多人时以分号分隔
	private String ccs;
	// 需要发送的人,多人时以分号分隔
	private String sends;
	// 对于定时任务,使用定时任务的 TriggerName;其它留空
	private String triggerName;
	// 邮件发送失败的时间,使用毫秒数
	private long failtime;
	//发送优先级,可取 1-5 共 5 个级别,数字越小优先级越高,默认为 5
	private int priority = 5;
	
	public void setContent(String content){
		this.content = content;
	}
	public String getContent(){
		return this.content;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return this.id;
	}
	public void setAttachmentNames(String attachmentNames){
		this.attachmentNames = attachmentNames;
	}
	public String getAttachmentNames(){
		return this.attachmentNames;
	}
	public void setAttachments(String attachments){
		this.attachments = attachments;
	}
	public String getAttachments(){
		return this.attachments;
	}
	public void setCcs(String ccs){
		this.ccs = ccs;
	}
	public String getCcs(){
		return this.ccs;
	}
	public void setSends(String sends){
		this.sends = sends;
	}
	public String getSends(){
		return this.sends;
	}
	public void setTriggerName(String triggerName){
		this.triggerName = triggerName;
	}
	public String getTriggerName(){
		return this.triggerName;
	}
	public long getFailtime() {
		return failtime;
	}
	public void setFailtime(long failtime) {
		this.failtime = failtime;
	}
	public int getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * 排序时使用优先级排序,
	 * 先发送优先级高的邮件
	 */
	@Override
	public int compareTo(MailSendQueue o) {
		return this.priority - o.priority;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String toString() {
		return this.priority + "";
	}
}

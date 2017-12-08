package berkeleyQueue;

import br.com.easydoc.berkeleyDBLib.annotation.Id;

public class Data {

	@Id
	private Long id;
	private String text;
	private long timestamp;

	public Data() {}
	
	public Data(String text) {
		this.text = text;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}

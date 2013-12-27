package simplify;

import java.util.List;

public class Method {
	
	private final String parentFile;
	
	private List<String> lines;
	
	public Method(String parentFile, List<String> lines) {
		this.parentFile = parentFile;
		this.lines = lines;
	}
	
	public String getParentFile() {
		return parentFile;
	}
	
	public String getLine(int line) {
		return lines.get(line);
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public void setLine(int line, String value) {
		lines.set(line,  value);
	}
	
	public void removeLine(int line) {
		lines.remove(line);
	}

}

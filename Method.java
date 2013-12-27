package simplify;

import java.util.List;
import java.util.Map;

public class Method {

    private final String parentFile;
    private final List<String> lines;
    private final Map<String, Integer> jumps;

    public Method(String parentFile, List<String> lines, Map<String, Integer> jumps) {
        this.parentFile = parentFile;
        this.lines = lines;
        this.jumps = jumps;
    }

    public String getParentFile() {
        return parentFile;
    }

    public String getLine(int line) {
        return lines.get(line);
    }

    public void addJump(String label, int line) {
        jumps.put(label, line);
    }

    public int getJumpPosition(String label) {
        return jumps.get(label);
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLine(int line, String value) {
        lines.set(line, value);
    }

    public void removeLine(int line) {
        lines.remove(line);
    }

}

package simplify;

import java.util.List;
import java.util.Map;

public class Method {

    private final String parentFile;
    private final String signature;
    private final List<String> lines;
    private final Map<String, Integer> jumps;

    private boolean modified;

    public Method(String parentFile, String signature, List<String> lines, Map<String, Integer> jumps) {
        modified = false;

        this.parentFile = parentFile;
        this.signature = signature;
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
        modified = true;
        lines.set(line, value);
    }

    public void removeLine(int line) {
        modified = true;
        lines.remove(line);
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public String toString() {
        return signature;
    }
}

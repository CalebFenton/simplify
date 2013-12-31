package simplify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import simplify.graph.Node;

public class Method {

    private final String classFile;
    private final String signature;
    private final List<String> lines;
    private final Map<Integer, Node> lineNodeMap;
    private final Map<String, Integer> jumpTable;

    private boolean modified;

    public Method(String classFile, String signature, List<String> lines) {
        this.classFile = classFile;
        this.signature = signature;
        this.lines = lines;

        lineNodeMap = new TreeMap<Integer, Node>();
        jumpTable = new HashMap<String, Integer>();

        modified = false;
    }

    public String getClassFile() {
        return classFile;
    }

    public String getLine(int line) {
        return lines.get(line);
    }

    public Map<Integer, Node> getLineNodeMap() {
        return lineNodeMap;
    }

    public Node getRootNode() {
        Node root = null;
        for (Integer key : lineNodeMap.keySet()) {
            root = lineNodeMap.get(key);
            break;
        }

        return root;
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

    public void addNode(int line, Node node) {
        lineNodeMap.put(line, node);
    }

    public void addJump(String label, int line) {
        jumpTable.put(label, line);
    }

    public Integer getJump(String label) {
        return jumpTable.get(label);
    }

    @Override
    public String toString() {
        return signature;
    }
}

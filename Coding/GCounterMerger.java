import java.util.*;

public class GCounterMerger {
    public static Map<String, Integer> mergeCounters(
            Map<String, Integer> counterA,
            Map<String, Integer> counterB) {
        // TODO: implement this method
        Map<String, Integer> merged = new HashMap<>(counterA);
        for (Map.Entry<String, Integer> entry : counterB.entrySet()) {
            merged.put(entry.getKey(), Math.max(merged.getOrDefault(entry.getKey(), 0), entry.getValue()));
        }
        return merged;
    }

    public static void main(String[] args) {
        // Test 1: basic merge with overlapping and non-overlapping keys
        Map<String, Integer> a = new HashMap<>();
        a.put("node1", 5);
        a.put("node2", 3);

        Map<String, Integer> b = new HashMap<>();
        b.put("node2", 7);
        b.put("node3", 2);
        Map<String, Integer> merged = mergeCounters(a, b);
        if (merged.get("node1") != 5) {
            System.out.println("FAIL Test 1a: node1 should be 5, got " +
                    merged.get("node1"));
            return;
        }
        if (merged.get("node2") != 7) {
            System.out.println("FAIL Test 1b: node2 should be max(3,7)=7, got " +
                    merged.get("node2"));
            return;
        }
        if (merged.get("node3") != 2) {
            System.out.println("FAIL Test 1c: node3 should be 2, got " +
                    merged.get("node3"));
            return;
        }
        int total = merged.values().stream().mapToInt(i -> i).sum();
        if (total != 14) {
            System.out.println("FAIL Test 1d: total count should be 14, got " +
                    total);
            return;
        }
        System.out.println("PASS Test 1: basic merge correct (node1=5, node2=7, node3=2, total=14)");
        // Test 2: merge with empty counter
        Map<String, Integer> empty = new HashMap<>();
        Map<String, Integer> merged2 = mergeCounters(a, empty);
        if (!merged2.equals(a)) {
            System.out.println("FAIL Test 2: merging with empty map should return the other map's values");
            return;
        }
        System.out.println("PASS Test 2: merge with empty counter correct");
        // Test 3: merge is commutative (order should not matter)
        Map<String, Integer> mergedAB = mergeCounters(a, b);
        Map<String, Integer> mergedBA = mergeCounters(b, a);
        if (!mergedAB.equals(mergedBA)) {
            System.out.println(
                    "FAIL Test 3: merge should be commutative - mergeCounters(a,b) must equal mergeCounters(b,a)");
            return;
        }
        System.out.println("PASS Test 3: merge is commutative");
        System.out.println("\nAll tests passed!");
    }
}

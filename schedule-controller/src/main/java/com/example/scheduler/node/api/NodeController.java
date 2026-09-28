package com.example.scheduler.node.api;

import com.example.scheduler.node.application.NodeRegistry;
import com.example.scheduler.node.domain.NodeView;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    private final NodeRegistry nodes;
    public NodeController(NodeRegistry nodes) { this.nodes = nodes; }
    @GetMapping public List<NodeView> list() { return nodes.list(); }
    @PostMapping("/{nodeId}/drain")
    public ResponseEntity<NodeView> drain(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.of(nodes.setDrain(nodeId, true));
    }
    @DeleteMapping("/{nodeId}/drain")
    public ResponseEntity<NodeView> resume(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.of(nodes.setDrain(nodeId, false));
    }
}

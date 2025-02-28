package com.compus.cns.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.compus.cns.dto.NodesDTO;
import com.compus.cns.model.Nodes;
import com.compus.cns.service.NodeService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/m/nodes")
@CrossOrigin(origins = "http://localhost:5173")
public class NodeController {
    @Autowired
    private NodeService nodeServ;

    @GetMapping("")
    public ResponseEntity<List<Nodes>> getNodes() {
        List<Nodes> nodes = nodeServ.getAllNodes();
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNodes(@RequestBody List<NodesDTO> data) {
    	List<Nodes> node = data.stream()
    							.map(this::convertToNode)
    							.collect(Collectors.toList());
        try {
            nodeServ.addNodes(node);
            return new ResponseEntity<>("Nodes Added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't add nodes", HttpStatus.CONFLICT);
        }
        
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateNodes(@RequestBody NodesDTO data) {
        try {
            List<Nodes> updatedNodes = nodeServ.updateNodesByDesc(data.getDesc(), data.getCoords());
            if (updatedNodes != null && !updatedNodes.isEmpty()) {
                return new ResponseEntity<>("Nodes Updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Node with the given description not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't update node", HttpStatus.CONFLICT);
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteNode(@RequestParam String desc) {
        try {
            boolean isDeleted = nodeServ.deleteNodeByDescription(desc);
            if (isDeleted) {
                return new ResponseEntity<>("Node Deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Node with description '" + desc + "' not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't delete node", HttpStatus.CONFLICT);
        }
    }

    
    private Nodes convertToNode(NodesDTO data) {
    	Nodes node = new Nodes();
    	node.setDescription(data.getDesc());
    	node.setCoords(data.getCoords());
    	
    	return node;
    }
}

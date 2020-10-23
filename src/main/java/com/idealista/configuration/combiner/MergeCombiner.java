package com.idealista.configuration.combiner;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;


public class MergeCombiner extends NodeCombiner {
    
    private MergeOptions mergeOptions;
    
    private ImmutableNode.Builder result;
        
    public MergeCombiner(MergeOptions mergeOptions) {
        this.mergeOptions = mergeOptions;        
        this.result = new ImmutableNode.Builder();
    }
    
    @Override
    public ImmutableNode combine(ImmutableNode baseNode, ImmutableNode otherNode) {
        fillResult(baseNode, otherNode);
        
        // Check if nodes can be combined
        List<ImmutableNode> otherNodeChildren = new LinkedList<>(otherNode.getChildren());
        
        for (ImmutableNode baseNodeChild : baseNode.getChildren()) {
            List<ImmutableNode> otherNodeMatchingChildren = HANDLER.getChildren(otherNode, baseNodeChild.getNodeName());
            
            if (otherNodeMatchingChildren.size() == 1) {
                ImmutableNode otherNodeMatchingFirstChild = otherNodeMatchingChildren.get(0);
                
                result.addChild(new MergeCombiner(mergeOptions).combine(baseNodeChild, otherNodeMatchingFirstChild));
                otherNodeChildren.remove(otherNodeMatchingFirstChild);
            } else {
                preserve(baseNodeChild);
            }
            
            if (otherNodeMatchingChildren.size() > 1 && !isListNode(baseNodeChild)) {
                otherNodeChildren.removeAll(otherNodeMatchingChildren);
            }
            
        }

        addNew(otherNodeChildren);
                
        return result.create();
    }

    private void addNew(List<ImmutableNode> otherNodeChildren) {
        if(!mergeOptions.isAdditionAllowed) return;
        result.addChildren(otherNodeChildren);
    }
    
    private void preserve(ImmutableNode baseNodeChild) {
        if(mergeOptions.isDeletionAllowed) return;
        result.addChild(baseNodeChild);
    }
    
    private void fillResult(ImmutableNode baseNode, ImmutableNode otherNode) {
        if(mergeOptions.isModificationAllowed) {
            fillResult(otherNode);
        } else {
            fillResult(baseNode);
        }
    }

    private void fillResult(ImmutableNode node) {
        result.name(node.getNodeName());
        Object value = node.getValue();
        if (mergeOptions.removeBaseEscapes && value instanceof String) {
            value = removeEscape((String)value);
        }
        result.value(value);
    }

    private Object removeEscape(String value) {
        return value.replace("\\","");
    }
}
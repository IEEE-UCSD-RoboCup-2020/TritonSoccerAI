package Triton.CoreModules.AI.TritonProbDijkstra.Exceptions;

import Triton.CoreModules.AI.TritonProbDijkstra.PUAG;

public class UnknownPuagNodeException extends RuntimeException{
    public UnknownPuagNodeException(PUAG.Node n){
        super("Received unknown PUAG node of type: " + n.getClass().getName());
    }
}

package Triton.CoreModules.AI.TritonProbDijkstra.ComputableImpl;

import Triton.Config.GlobalVariblesAndConstants.GvcGeometry;
import Triton.CoreModules.AI.Estimators.PassInfo;
import Triton.CoreModules.AI.Estimators.ProbMapModule;
import Triton.CoreModules.AI.Estimators.Score;
import Triton.CoreModules.AI.Estimators.Scores.*;
import Triton.CoreModules.AI.Estimators.TimeEstimator.BallMovement;
import Triton.CoreModules.AI.TritonProbDijkstra.Computables.DijkCompute;
import Triton.CoreModules.AI.TritonProbDijkstra.Exceptions.NonExistentNodeException;
import Triton.CoreModules.AI.TritonProbDijkstra.PDG;
import Triton.CoreModules.Robot.RobotSnapshot;
import Triton.Misc.Math.Geometry.Rect2D;
import Triton.Misc.Math.LinearAlgebra.Vec2D;
import Triton.Misc.RWLockee;
import java.util.ArrayList;
import java.util.HashMap;

import static Triton.Config.GlobalVariblesAndConstants.GvcGeometry.TOP_TOUCH_LINE;
import static Triton.Config.GlobalVariblesAndConstants.GvcGeometry.BOTTOM_TOUCH_LINE;
import static Triton.Misc.Math.Coordinates.PerspectiveConverter.audienceToPlayer;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

@Getter
@Setter
public class Compute implements DijkCompute {

    private static final double C1_WEIGHT = 1.0;
    private static final double C2_WEIGHT = 1.0;
    private static final double C3_WEIGHT = 1.0;
    private static final double C4_WEIGHT = 1.0;
    private static final double C5_WEIGHT = 1.0;

    private static final double G1_WEIGHT = 1.0;
    private static final double G2_WEIGHT = 1.0;

    private static final double SAMPLE_PADDING  = 250.0;
    private static final double SAMPLE_INTERVAL = 50.0;

    private static final double GOAL_KICK_TIME = 1.0;
    private static final double MAX_KICK_VEL = 6.4;
    private static final double MIN_KICK_VEL = 1.0;

    private ArrayList<RobotSnapshot> fielderSnaps;
    private ArrayList<RobotSnapshot> foeSnaps;
    private Vec2D ballPos;
    private PassInfo[][] infoMatrix;
    private Rect2D allyPenaltyRegion, foePenaltyRegion;

    private PDG graph;
    private HashMap<PDG.Node, Integer> nodeToIndexMap;

    public Compute(PDG graph) {
        this.graph = graph;
        nodeToIndexMap = graph.getNodeToIndexMap();
        assert graph.getNumNodes() == nodeToIndexMap.size();

        int workingSize = nodeToIndexMap.size();
        infoMatrix = new PassInfo[workingSize][workingSize];

        Rect2D[] penaltyRegions = ProbMapModule.getPenaltyRegions();
        allyPenaltyRegion = penaltyRegions[0];
        foePenaltyRegion = penaltyRegions[1];
    }

    public int getIndexOfNode(PDG.Node n) throws NonExistentNodeException {
        Integer integer = nodeToIndexMap.get(n);
        if(integer == null){
            throw new NonExistentNodeException(n);
        }
        return integer;
    }

    public void computePass(PDG.Node n1, PDG.Node n2) throws NonExistentNodeException {
        /* Return if have computed for current data */
        if (infoMatrix[getIndexOfNode(n1)][getIndexOfNode(n2)] != null) return;

        assert n1.getBot() != null && n2.getBot() != null;

        /* Initiate scores */
        Score c1 = new C1(ballPos, fielderSnaps, foeSnaps, n2.getBot().getID(), true);
        Score c2 = new C2(ballPos, fielderSnaps, foeSnaps, true);
        Score c3 = new C3(ballPos, fielderSnaps, foeSnaps);
        Score c4 = new C4(ballPos, fielderSnaps, foeSnaps);
        Score c5 = new C5(ballPos, fielderSnaps, foeSnaps, allyPenaltyRegion, foePenaltyRegion);

        /* Evaluate reception points */
        double[] bnds = getMinMax(n1.getBot().getPos(), n2.getBot().getPos());
        Vec2D corner1 = audienceToPlayer(TOP_TOUCH_LINE.p2);
        Vec2D corner2 = audienceToPlayer(BOTTOM_TOUCH_LINE.p1);
        double[] fields = getMinMax(corner1, corner2);

        Vec2D receivingPos = null;
        double prob = -Double.MAX_VALUE;

        for (double x = bnds[0] - SAMPLE_PADDING; x < bnds[1] + SAMPLE_PADDING; x += SAMPLE_INTERVAL) {
            for (double y = bnds[2] - SAMPLE_PADDING; y < bnds[3] + SAMPLE_PADDING; y += SAMPLE_INTERVAL) {
                if (x < fields[0] || x > fields[1] || y < fields[2] || y > fields[3]) {
                    continue; // out of field
                }
                Vec2D pos = new Vec2D(x, y);

                double c1prob = c1.prob(pos);
                double c2prob = c2.prob(pos);
                double c3prob = c3.prob(pos);
                double c4prob = c4.prob(pos);
                double c5prob = c5.prob(pos);

                double c = c1prob * C1_WEIGHT + c2prob * C2_WEIGHT + c3prob * C3_WEIGHT
                        + c4prob * C4_WEIGHT + c5prob * C5_WEIGHT;
                double cprob = (1 / (1 + Math.exp(-c)));
                if (cprob > prob) {
                    prob = cprob;
                    receivingPos = pos;
                }
            }
        }

        PassInfo info = new PassInfo();
        info.setInfo(n1.getBot(), n2.getBot(), n1.getBot().getPos(), receivingPos, prob);
        infoMatrix[getIndexOfNode(n1)][getIndexOfNode(n2)] = info;
    }

    private static double[] getMinMax(Vec2D v1, Vec2D v2) {
        double xmin, xmax, ymin, ymax;
        xmin = v1.x;
        xmax = v2.x;
        if (xmax < xmin) {
            double temp = xmin;
            xmin = xmax;
            xmax = temp;
        }
        ymin = v1.y;
        ymax = v2.y;
        if (ymax < ymin) {
            double temp = ymin;
            ymin = xmax;
            ymax = temp;
        }
        return new double[]{xmin, xmax, ymin, ymax};
    }

    @Override
    public double computeAngle(PDG.Node n1, PDG.Node n2) {
        assert n1.getBot() != null;

        Vec2D path = n2.getPos().sub(n1.getPos());
        return path.toPlayerAngle();
    }



    @Override
    public void setSnapShots(ArrayList<RobotSnapshot> allySnaps, ArrayList<RobotSnapshot> foeSnaps,
                             RWLockee<Vec2D> ballSnap) {
        this.fielderSnaps = allySnaps;
        this.foeSnaps = foeSnaps;
        this.ballPos = ballSnap.get();
        int workingSize = nodeToIndexMap.size();
        infoMatrix = new PassInfo[workingSize][workingSize];
    }

    @Override
    public Vec2D computeKickVec(PDG.Node n1, PDG.Node n2) throws NonExistentNodeException {
        computePass(n1, n2);
        PassInfo info = infoMatrix[getIndexOfNode(n1)][getIndexOfNode(n2)];
        info.setRobots(fielderSnaps, foeSnaps);
        Pair<Vec2D, Boolean> kick = info.getKickDecision();
        return kick.getValue0();
    }

    @Override
    public double computeGoalProb(PDG.Node n) {
        assert n.getBot() != null;
        Vec2D pos = n.getBot().getPos();

        /* Initiate scores */
        Score g1 = new G1(ballPos, fielderSnaps, foeSnaps, true);
        Score g2 = new G2(ballPos, fielderSnaps, foeSnaps);

        /* Evaluate goal probability */
        double g1prob = g1.prob(pos);
        double g2prob = g2.prob(pos);
        double g = g1prob * G1_WEIGHT + g2prob * G2_WEIGHT;

        return (1 / (1 + Math.exp(-g)));
    }

    @Override
    public Vec2D computeGoalKickVec(PDG.Node node) {
        Vec2D goal = computeGoalCenter();
        double ballDist = goal.sub(node.getPos()).mag();
        double s = BallMovement.calcKickVel(ballDist, GOAL_KICK_TIME);
        return new Vec2D(Math.max(MIN_KICK_VEL, Math.min(s, MAX_KICK_VEL)), 0);
    }

    @Override
    public Vec2D computeGoalPassPoint(PDG.Node node) {
        return computePassPoint(node);
    }

    @Override
    public double computeGoalAngle(PDG.Node n) {
        Vec2D path = GvcGeometry.GOAL_CENTER_FOE.sub(n.getPos());
        return path.toPlayerAngle();
    }

    @Override
    public Vec2D computeGoalCenter() {
        return foePenaltyRegion.anchor.add(new Vec2D(foePenaltyRegion.width / 2.0, foePenaltyRegion.height));
    }

    @Override
    public Vec2D computePassPoint(PDG.Node n1, PDG.Node n2) {
        assert n1.getBot() != null;
        return n1.getBot().getPos();
    }

    @Override
    public Vec2D computePassPoint(PDG.Node node) {
        return node.getPos();
    }



    @Override
    public double computeProb(PDG.Node n1, PDG.Node n2) throws NonExistentNodeException {
        computePass(n1, n2);
        return infoMatrix[getIndexOfNode(n1)][getIndexOfNode(n2)].getMaxProb();
    }



    @Override
    public Vec2D computeRecepPoint(PDG.Node n1, PDG.Node n2) throws NonExistentNodeException {
        computePass(n1, n2);
        return infoMatrix[getIndexOfNode(n1)][getIndexOfNode(n2)].getOptimalReceivingPos();
    }
}

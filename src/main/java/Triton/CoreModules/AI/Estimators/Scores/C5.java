package Triton.CoreModules.AI.Estimators.Scores;

import Triton.CoreModules.AI.Estimators.ProbFinder;
import Triton.CoreModules.AI.Estimators.Score;
import Triton.Misc.Math.Geometry.Rect2D;
import Triton.Misc.Math.Matrix.Vec2D;

import static Triton.Config.OldConfigs.GeometryConfig.FIELD_LENGTH;
import static Triton.Config.OldConfigs.GeometryConfig.FIELD_WIDTH;

/**
 * c5: Location x is reliable for pass reception
 */
public class C5 extends Score {

    private static final double C5_MAX_DIST = 500.0;
    private static final double C5_DEV = 100.0;
    private final Rect2D allyPenaltyRegion;
    private final Rect2D foePenaltyRegion;

    public C5(ProbFinder finder, Rect2D allyPenaltyRegion, Rect2D foePenaltyRegion) {
        super(finder);
        this.allyPenaltyRegion = allyPenaltyRegion;
        this.foePenaltyRegion  = foePenaltyRegion;
    }

    @Override
    public double prob(Vec2D pos) {
        if (allyPenaltyRegion.isInside(pos) || foePenaltyRegion.isInside(pos)) {
            return - Double.MAX_VALUE; // negative infinity
        }

        double penaltyDist = Math.min(allyPenaltyRegion.distTo(pos), foePenaltyRegion.distTo(pos));
        double xDist = Math.min(Math.abs(pos.x - FIELD_WIDTH / 2), Math.abs(pos.x + FIELD_WIDTH / 2));
        double yDist = Math.min(Math.abs(pos.y - FIELD_LENGTH / 2), Math.abs(pos.y + FIELD_LENGTH / 2));
        return Math.min(0, penaltyDist - C5_MAX_DIST) / C5_DEV
                + Math.min(0, xDist - C5_MAX_DIST) / C5_DEV
                + Math.min(0, yDist - C5_MAX_DIST) / C5_DEV;
    }
}

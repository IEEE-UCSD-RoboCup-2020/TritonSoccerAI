package Triton.CoreModules.Robot.Ally.AdvancedSkills;

import Triton.Config.GlobalVariblesAndConstants.GvcPathfinder;
import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally.Ally;
import Triton.Misc.Math.LinearAlgebra.Vec2D;

public class GetBall {
    public static void exec(Ally ally, Ball ball) {
        Vec2D ballLoc = ball.getPos();
        Vec2D currPos = ally.getPos();
        Vec2D currPosToBall = ballLoc.sub(currPos);
        if (currPosToBall.mag() <= GvcPathfinder.AUTOCAP_DIST_THRESH) {
            ally.autoCap();
        } else {
            ally.curveTo(ballLoc, currPosToBall.toPlayerAngle());
            //dynamicIntercept(ball, 0);
        }
    }
}

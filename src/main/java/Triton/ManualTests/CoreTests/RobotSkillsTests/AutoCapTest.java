package Triton.ManualTests.CoreTests.RobotSkillsTests;

import Triton.Config.Config;
import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally.Ally;
import Triton.ManualTests.TritonTestable;

import java.time.LocalDateTime;

public class AutoCapTest implements TritonTestable {

    private final Ally ally;
    private final Ball ball;

    public AutoCapTest(Ally ally, Ball ball) {
        this.ally = ally;
        this.ball = ball;
    }

    @Override
    public boolean test(Config config) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime later = now.plusSeconds(20);

        while(!ally.isHoldingBall() && LocalDateTime.now().isBefore(later)) {
            ally.autoCap();
        }

        return true;
    }
}

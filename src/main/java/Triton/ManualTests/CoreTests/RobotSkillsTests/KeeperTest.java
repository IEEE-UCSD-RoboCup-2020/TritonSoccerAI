package Triton.ManualTests.CoreTests.RobotSkillsTests;

import Triton.Config.Config;
import Triton.CoreModules.AI.Estimators.BasicEstimator;
import Triton.CoreModules.AI.GoalKeeping.GoalKeeping;
import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally.Ally;
import Triton.CoreModules.Robot.Foe.Foe;
import Triton.CoreModules.Robot.RobotList;

public class KeeperTest extends RobotSkillsTest {
    RobotList<Ally> fielders;
    Ally keeper;
    RobotList<Foe> foes;
    Ball ball;

    BasicEstimator basicEstimator;
    GoalKeeping goalKeeping;

    public KeeperTest(RobotList<Ally> fielders, Ally keeper, RobotList<Foe> foes, Ball ball) {
        this.fielders = fielders;
        this.keeper = keeper;
        this.foes = foes;
        this.ball = ball;

        basicEstimator = new BasicEstimator(fielders, keeper, foes, ball);
        goalKeeping = new GoalKeeping(keeper, ball, basicEstimator);
    }

    @Override
    public boolean test(Config config) {
        while (true) {
            goalKeeping.passiveGuarding();
        }
    }
}

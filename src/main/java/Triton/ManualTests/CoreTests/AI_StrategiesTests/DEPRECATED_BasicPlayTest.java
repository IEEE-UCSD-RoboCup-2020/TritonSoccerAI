package Triton.ManualTests.CoreTests.AI_StrategiesTests;

import Triton.Config.Config;
import Triton.CoreModules.AI.AI_Strategies.DEPRECATED_BasicPlay;
import Triton.CoreModules.AI.Estimators.AttackSupportMapModule;
import Triton.CoreModules.AI.Estimators.PassProbMapModule;
import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally.Ally;
import Triton.CoreModules.Robot.Foe.Foe;
import Triton.CoreModules.Robot.RobotList;
import Triton.ManualTests.TritonTestable;

public class DEPRECATED_BasicPlayTest implements TritonTestable {
    private AttackSupportMapModule atkSupportMap;
    private PassProbMapModule passProbMap;
    DEPRECATED_BasicPlay basicPlay;
    private final RobotList<Ally> fielders;
    private final Ally keeper;
    private final RobotList<Foe> foes;
    private final Ball ball;
    public DEPRECATED_BasicPlayTest(RobotList<Ally> fielders, Ally keeper, RobotList<Foe> foes, Ball ball) {
        this.fielders = fielders;
        this.keeper = keeper;
        this.foes = foes;
        this.ball = ball;
    }

    public boolean test(Config config) {
        atkSupportMap = new AttackSupportMapModule(fielders, foes, ball);
        passProbMap = new PassProbMapModule(fielders, foes, ball);
        basicPlay = new DEPRECATED_BasicPlay(fielders, keeper, foes, ball, atkSupportMap, passProbMap, config);
        try {
            while (true) {
                basicPlay.play();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}

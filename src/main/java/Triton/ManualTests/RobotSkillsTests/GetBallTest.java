package Triton.ManualTests.RobotSkillsTests;

import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally;

import java.util.Scanner;


public class GetBallTest extends RobotSkillsTest {
    Ally ally;
    Ball ball;

    public GetBallTest(Ally ally, Ball ball) {
        this.ally = ally;
        this.ball = ball;
    }

    @Override
    public boolean test() {
//        String line;
//        do {
//            System.out.println(">> ENTER \"start\" TO START:");
//            line = scanner.nextLine();
//        } while (!line.equals("start"));

        while (!ally.isHoldingBall()) {
            ally.getBall(ball);
        }

        ally.stop();

        return true;
    }
}

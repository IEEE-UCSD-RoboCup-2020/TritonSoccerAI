package Triton.ManualTests;

import Triton.CoreModules.AI.Formation;
import Triton.CoreModules.Ball.Ball;
import Triton.CoreModules.Robot.Ally;
import Triton.CoreModules.Robot.Foe;
import Triton.CoreModules.Robot.RobotList;
import Triton.ManualTests.AISkillsTests.CPassTest;
import Triton.ManualTests.AISkillsTests.GroupToTest;
import Triton.ManualTests.RobotSkillsTests.*;
import Triton.Misc.ModulePubSubSystem.Module;

import java.util.Scanner;

public class TestRunner implements Module {

    private final Scanner scanner;
    private final RobotList<Ally> fielders;
    private final Ally keeper;
    private final RobotList<Foe> foes;
    private final Ball ball;

    private String testName;

    public TestRunner(RobotList<Ally> fielders,
                      Ally keeper, RobotList<Foe> foes, Ball ball) {
        this.fielders = fielders;
        this.keeper = keeper;
        this.foes = foes;
        this.ball = ball;

        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            while (!Formation.getInstance().testerFormation(fielders)) {
                Thread.sleep(1);
            }

            String prevTestName = "";
            boolean quit = false;
            while (!quit) {
                System.out.println(">> ENTER TEST NAME:");
                testName = scanner.nextLine();
                boolean rtn = false;
                int repeat = 0;
                do {
                    switch (testName) {
                        case "pmotion" -> rtn = new PrimitiveMotionTest(scanner, fielders.get(3)).test();
                        case "getball" -> rtn = new GetBallTest(scanner, fielders.get(3), ball).test();
                        case "kick" -> rtn = new KickTest(scanner, fielders.get(3), ball).test();
                        case "misc" -> rtn = new MiscTest(scanner, fielders.get(3), ball).test();
                        case "pass" -> rtn = new PassTest(scanner, fielders.get(3), ball).test();
                        case "cpass" -> rtn = new CPassTest(scanner, fielders,  keeper, foes, ball).test();
                        case "group" -> rtn = new GroupToTest(scanner, fielders).test();
                        case "quit" -> quit = true;
                        case "" -> {
                            repeat++;
                            testName = prevTestName;
                        }
                        default -> System.out.println("Invalid Test Name");
                    }
                } while (repeat-- > 0);
                repeat = 0;
                prevTestName = testName;
                System.out.println(rtn ? "Test Success" : "Test Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}